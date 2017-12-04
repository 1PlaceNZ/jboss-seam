//$Id: TodoTest.java 5299 2007-06-20 00:16:21Z gavin $
package org.jboss.seam.example.todo.test;

import java.io.File;
import java.util.List;

import org.jboss.seam.example.todo.Login;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.bpm.TaskInstanceList;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class TodoTest extends JUnitSeamTest
{
   @Deployment(name = "TodoTest")
   @OverProtocol("Servlet 3.0")
   public static Archive<?> createDeployment()
   {
      File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
                .importCompileAndRuntimeDependencies()
                // force resolve jboss-seam, because it is provided-scoped in the pom, but we need it bundled in the WAR
                .resolve("org.jboss.seam:jboss-seam")
                .withTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class, "seam-todo.war")
                .addPackage(Login.class.getPackage())
                .addAsWebInfResource("components-test.xml","components.xml")
                .addAsWebInfResource("jboss-deployment-structure.xml")
                .addAsResource("seam.properties")
                .addAsWebInfResource("web.xml")
                .addAsWebInfResource("jbpm.cfg.xml", "classes/jbpm.cfg.xml")
                .addAsWebInfResource("hibernate.cfg.xml", "classes/hibernate.cfg.xml")
                .addAsWebInfResource("todo.jpdl.xml", "classes/todo.jpdl.xml")
                .addAsLibraries(libs);
   }
   
   private long taskId;
   
   @Test
   public void testTodo() throws Exception
   {
      
      new FacesRequest() {

         @Override
         protected void updateModelValues() throws Exception
         {
            setValue("#{login.user}", "gavin");
         }

         @Override
         protected void invokeApplication() throws Exception
         {
            assert invokeMethod("#{login.login}").equals("/todo.jsp");
            assert Actor.instance().getId().equals("gavin");
         }

         @Override
         protected void renderResponse() throws Exception
         {
            assert ( (List) getInstance(TaskInstanceList.class) ).size()==0;
         }
         
      }.run();
      
      new FacesRequest()
      {

         @Override
         protected void updateModelValues() throws Exception
         {
            setValue("#{todoList.description}", "Kick Roy out of my office");
         }

         @Override
         protected void invokeApplication() throws Exception
         {
            invokeMethod("#{todoList.createTodo}");
         }
         
         @Override
         protected void renderResponse() throws Exception
         {
            List<TaskInstance> tasks = (List<TaskInstance>) getInstance(TaskInstanceList.class);
            assert tasks.size()==1;
            TaskInstance taskInstance = tasks.get(0);
            assert taskInstance.getDescription().equals("Kick Roy out of my office");
            taskId = taskInstance.getId();
         }
         
      }.run();

   
      new FacesRequest()
      {
   
         @Override
         protected void beforeRequest()
         {
            setParameter("taskId", Long.toString(taskId));
         }

         @Override
         protected void invokeApplication() throws Exception
         {
            invokeMethod("#{todoList.done}");
         }
         
         @Override
         protected void renderResponse() throws Exception
         {
            assert ( (List) getInstance(TaskInstanceList.class) ).size()==0;
         }
         
      }.run();
   }
   
}
