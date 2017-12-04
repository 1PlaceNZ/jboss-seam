package org.jboss.seam.example.blog.test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.seam.security.Identity;
import org.jboss.seam.theme.Theme;
import org.jboss.seam.theme.ThemeSelector;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import org.junit.Test;
import org.junit.runner.RunWith;

import actions.BlogService;
import actions.SearchService;
import domain.Blog;
import domain.BlogEntry;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

@RunWith(Arquillian.class)
public class BlogTest extends JUnitSeamTest {

    @Deployment(name = "BestSellersTest")
    @OverProtocol("Servlet 3.0")
    public static Archive<?> createDeployment() {
        
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
                .importCompileAndRuntimeDependencies()
                // force resolve jboss-seam, because it is provided-scoped in the pom, but we need it bundled in the WAR
                .resolve("org.jboss.seam:jboss-seam")
                .withTransitivity().asFile();

        WebArchive war = ShrinkWrap.create(WebArchive.class, "seam-blog.war")
                .addPackages(true, "actions", "domain")
                // already in EJB module
                .addAsResource("import.sql")
                .addAsResource("seam.properties")
                .addAsResource("META-INF/persistence.xml")
                
                // manually copied from Web module
                .addAsWebInfResource("pages.xml")
                .addAsResource("accessible.properties")
                .addAsResource("default.properties")
                .addAsResource("printable.properties")
                .addAsResource("infinispan.xml")
                .addAsResource("jgroupsConfig.xml")
                
                // manually copied from Web module, modified
                .addAsWebInfResource("web.xml") // only contains MockSeamListener definition
                .addAsWebInfResource("components-test.xml", "components.xml") // corrected ejb component jndi-name references from java:app/jboss-seam to java:app/seam-blog

                // manually copied from EAR module
                .addAsWebInfResource("jboss-deployment-structure.xml")
                .addAsLibraries(libs);

        return war;
    }

    @Test
    @InSequence(1)
    public void testPost() throws Exception {
        new FacesRequest() {
            @Override
            protected void updateModelValues() throws Exception {
                Identity.instance().setPassword("tokyo");
            }

            @Override
            protected void invokeApplication() throws Exception {
                Identity.instance().authenticate();
            }
        }.run();

        new FacesRequest("/post.xhtml") {
            @Override
            protected void updateModelValues() throws Exception {
                BlogEntry entry = (BlogEntry) getInstance("blogEntry");
                entry.setId("testing");
                entry.setTitle("Integration testing Seam applications is easy!");
                entry.setBody("This post is about SeamTest...");
            }

            @Override
            protected void invokeApplication() throws Exception {
                // post now returns void
                // assert invokeMethod("#{postAction.post}").equals("/index.xhtml");
                invokeMethod("#{postAction.post}");
                setOutcome("/index.xhtml");
            }

            @Override
            protected void afterRequest() {
                assert isInvokeApplicationComplete();
                assert !isRenderResponseBegun();
            }
        }.run();

        new NonFacesRequest("/index.xhtml") {
            @Override
            protected void renderResponse() throws Exception {
                List<BlogEntry> blogEntries = ((Blog) getInstance(BlogService.class)).getBlogEntries();
                assert blogEntries.size() == 4;
                BlogEntry blogEntry = blogEntries.get(0);
                assert blogEntry.getId().equals("testing");
                assert blogEntry.getBody().equals("This post is about SeamTest...");
                assert blogEntry.getTitle().equals("Integration testing Seam applications is easy!");
            }
        }.run();

        new FacesRequest() {
            @Override
            protected void invokeApplication() throws Exception {
                ((EntityManager) getInstance("entityManager")).createQuery("delete from BlogEntry where id='testing'").executeUpdate();
            }
        }.run();

    }

    @Test
    @InSequence(2)
    public void testLatest() throws Exception {
        new NonFacesRequest("/index.xhtml") {
            @Override
            protected void renderResponse() throws Exception {
                assert ((Blog) getInstance(BlogService.class)).getBlogEntries().size() == 3;
            }
        }.run();
    }

    @Test
    @InSequence(3)
    public void testEntry() throws Exception {
        new NonFacesRequest("/entry.xhtml") {
            @Override
            protected void beforeRequest() {
                setParameter("blogEntryId", "seamtext");
            }

            @Override
            protected void renderResponse() throws Exception {
                BlogEntry blogEntry = (BlogEntry) Contexts.getEventContext().get("blogEntry");
                assert blogEntry != null;
                assert blogEntry.getId().equals("seamtext");

                // make sure the entry is really there
                assert blogEntry.getBody().length() > 0;
                assert blogEntry.getTitle().equals("Introducing Seam Text");
            }
        }.run();
    }

    @Test
    @InSequence(4)
    public void testSearch() throws Exception {
        // Some time to allow indexing in the background
        Thread.sleep(1000);

        String id = new FacesRequest() {
            @Override
            protected void updateModelValues() throws Exception {
                ((SearchService) getInstance(SearchService.class)).setSearchPattern("seam");
            }

            @Override
            protected String getInvokeApplicationOutcome() {
                return "/search.xhtml";
            }

            @Override
            protected void afterRequest() {
                assert !isRenderResponseBegun();
            }
        }.run();

        new NonFacesRequest("/search.xhtml", id) {
            @Override
            protected void beforeRequest() {
                setParameter("searchPattern", "\"seam text\"");
            }

            @Override
            protected void renderResponse() throws Exception {
                List<BlogEntry> results = (List<BlogEntry>) getInstance("searchResults");
                // The hibernate search returns non-precise matches since version 4, so we only check that the expected result is first
                assert !results.isEmpty();
                assert "seamtext".equals(results.get(0).getId());
            }
        }.run();
    }

    @Test
    @InSequence(5)
    public void testSelectTheme() throws Exception {
        String id = new NonFacesRequest("/index.xhtml") {
            @Override
            protected void renderResponse() throws Exception {
                List<SelectItem> themes = (List<SelectItem>) getValue("#{themeSelector.themes}");
                assert themes.size() == 3;
                assert themes.get(0).getLabel().equals("default");
                assert themes.get(0).getLabel().equals("default");
                assert "default".equals(getValue("#{themeSelector.theme}"));

                assert "template.xhtml".equals(getValue("#{theme.template}"));
                // we can't do interpolate the value correctly in these tests
                // assert "/screen.css".equals(getValue("#{theme.css}"));
                assert "foo".equals(getValue("#{theme.foo}"));
            }
        }.run();

        new FacesRequest("/index.xhtml", id) {
            @Override
            protected void updateModelValues() throws Exception {
                setValue("#{themeSelector.theme}", "accessible");
            }

            @Override
            protected void invokeApplication() throws Exception {
                invokeAction("#{themeSelector.select}");
            }

            @Override
            protected void renderResponse() throws Exception {
                assert "accessible".equals(getValue("#{themeSelector.theme}"));
                //assert "/accessible.css".equals(getValue("#{theme.css}"));
                assert "template.xhtml".equals(getValue("#{theme.template}"));
            }
        }.run();

        new FacesRequest("/index.xhtml", id) {
            @Override
            protected void invokeApplication() throws Exception {
                invokeAction("#{themeSelector.selectTheme('printable')}");
            }

            @Override
            protected void renderResponse() throws Exception {
                assert "printable".equals(getValue("#{themeSelector.theme}"));
                //assert "/printable.css".equals(getValue("#{theme.css}"));
                assert "print.xhtml".equals(getValue("#{theme.template}"));
                Map<String, String> theme = Theme.instance();
                assert theme.entrySet().size() == 2;
            }
        }.run();

        new FacesRequest("/index.xhtml", id) {
            @Override
            protected void updateModelValues() throws Exception {
                setValue("#{themeSelector.theme}", "foo");
            }

            @Override
            protected void invokeApplication() throws Exception {
                invokeAction("#{themeSelector.select}");
            }

            @Override
            protected void renderResponse() throws Exception {
                assert "foo".equals(getValue("#{themeSelector.theme}"));
                Map<String, String> theme = Theme.instance();
                ResourceBundle themeResources = ThemeSelector.instance().getThemeResourceBundle();
                assert !themeResources.getKeys().hasMoreElements();
                assert theme.entrySet().size() == 0;
                boolean exception = false;
                try {
                    themeResources.getObject("bar");
                } catch (MissingResourceException e) {
                    exception = true;
                }
                assert exception;
                assert theme.get("bar").equals("bar");
            }
        }.run();
    }
}
