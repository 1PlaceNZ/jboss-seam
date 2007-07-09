package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.wiki.core.model.Feed;
import org.jboss.seam.wiki.util.WikiUtil;
import org.ajax4jsf.dnd.event.DropEvent;

import javax.faces.application.FacesMessage;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Date;

@Name("directoryHome")
@Scope(ScopeType.CONVERSATION)
public class DirectoryHome extends NodeHome<Directory> {

    /* -------------------------- Context Wiring ------------------------------ */

    @DataModel
    List<Node> childNodes;

    @DataModelSelection
    Node selectedChildNode;

    /* -------------------------- Internal State ------------------------------ */

    private List<Document> childDocuments = new ArrayList<Document>();
    public List<Document> getChildDocuments() { return childDocuments; }

    /* -------------------------- Basic Overrides ------------------------------ */

    @Override
    public void create() {
        super.create();

        // Fill the datamodel for outjection
        refreshChildNodes();

        // Feed checkbox
        hasFeed = getInstance().getFeed()!=null;
    }

    /* -------------------------- Custom CUD ------------------------------ */

    @Override
    public String persist() {

        if (getParentDirectory().getParent() != null) {
            // This is a subdirectory in an area
            getInstance().setAreaNumber(getParentDirectory().getAreaNumber());
            return super.persist();
        } else {
            // This is a logical area in the wiki root

            // Satisfy NOT NULL constraint
            getInstance().setAreaNumber(Long.MAX_VALUE);

            // Do the persist() first, we need the identifier after this
            String outcome = super.persist();

            getInstance().setAreaNumber(getInstance().getId());

            // And flush() again...
            getEntityManager().flush();
            return outcome;
        }
    }

    protected boolean beforePersist() {
        createOrRemoveFeed();
        return super.preparePersist();
    }

    protected boolean beforeUpdate() {
        createOrRemoveFeed();
        return super.beforeUpdate();
    }

    protected boolean prepareRemove() {
        if (getInstance().getParent() == null) return false; // Veto wiki root delete
        return true;
    }

    /* -------------------------- Internal Methods ------------------------------ */

    private void refreshChildNodes() {
        childNodes = getInstance().getChildren();
        for (Node childNode : childNodes) {
            if (childNode instanceof Document) childDocuments.add((Document)childNode);
        }
    }

    @Transactional
    private void createOrRemoveFeed() {
        if (hasFeed && getInstance().getFeed() == null) {
            // Does have no feed but user wants one, create it
            Feed feed = new Feed();
            feed.setDirectory(getInstance());
            feed.setAuthor(getInstance().getCreatedBy().getFullname());
            feed.setTitle(getInstance().getName());
            getInstance().setFeed(feed);

            getFacesMessages().addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_INFO,
                "feedCreated",
                "Created syndication feed for this directory");

        } else if (!hasFeed && getInstance().getFeed() != null) {
            // Does have feed but user doesn't want it anymore... delete it
            getEntityManager().joinTransaction();
            getEntityManager().remove(getInstance().getFeed());
            getInstance().setFeed(null);

            getFacesMessages().addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_INFO,
                "feedRemoved",
                "Removed syndication feed of this directory");
        } else if (getInstance().getFeed() != null) {
            // Does have a feed and user still wants it, update the feed
            getInstance().getFeed().setTitle(getInstance().getName());
            getInstance().getFeed().setAuthor(getInstance().getCreatedBy().getFullname());
        }
    }

    /* -------------------------- Public Features ------------------------------ */

    private boolean hasFeed;

    public boolean isHasFeed() {
        return hasFeed;
    }

    public void setHasFeed(boolean hasFeed) {
        this.hasFeed = hasFeed;
    }

    public void resetFeed() {
        if (getInstance().getFeed() != null) {
            getLog().debug("resetting feed of directory");
            getInstance().getFeed().getFeedEntries().clear();
            getInstance().getFeed().setPublishedDate(new Date());
            getFacesMessages().addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_INFO,
                "feedReset",
                "Queued removal of all feed entries from the syndication feed of this directory, please update to finalize");
        }
    }

    @Restrict("#{s:hasPermission('Node', 'editMenu', directoryHome.instance)}")
    public void dropMenuItem(DropEvent event) {

        Node draggedObject = (Node)event.getDragValue();
        int currentPosition = getInstance().getChildren().indexOf(draggedObject);
        int newPosition = (Integer)event.getDropValue();

        if (currentPosition != newPosition) {
            WikiUtil.shiftListElement(getInstance().getChildren(), currentPosition, newPosition);
            refreshChildNodes();
        }

    }


}
