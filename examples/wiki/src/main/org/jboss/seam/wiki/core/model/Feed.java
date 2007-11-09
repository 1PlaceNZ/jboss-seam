/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.model;

import javax.persistence.*;
import java.util.*;
import java.io.Serializable;

@Entity
@Table(name = "FEED")
@org.hibernate.annotations.BatchSize(size = 10)
public class Feed implements Serializable {

    @Id
    @GeneratedValue(generator = "wikiSequenceGenerator")
    @Column(name = "FEED_ID")
    private Long id;

    @Version
    @Column(name = "OBJ_VERSION", nullable = false)
    protected Integer version;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "DESCRIPTION", nullable = true)
    private String description;

    @Column(name = "AUTHOR", nullable = false)
    private String author;

    @Column(name = "PUBLISHED_ON", nullable = false, updatable = false)
    private Date publishedDate = new Date();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DIRECTORY_ID", nullable = false, updatable = false)
    @org.hibernate.annotations.ForeignKey(name = "FK_FEED_DIRECTORY_ID")
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private Directory directory;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "FEED_FEEDENTRY",
        joinColumns = @JoinColumn(name = "FEED_ID", nullable = false, updatable = false),
        inverseJoinColumns= @JoinColumn(name = "FEEDENTRY_ID", nullable = false, updatable = false)
    )
    @org.hibernate.annotations.ForeignKey(name = "FK_FEED_FEEDENTRY_FEED_ID", inverseName = "FK_FEED_FEEDENTRY_FEEDENTRY_ID")
    @org.hibernate.annotations.Sort(type = org.hibernate.annotations.SortType.NATURAL)
    private SortedSet<FeedEntry> feedEntries = new TreeSet<FeedEntry>();

    public Feed() { }

    // Immutable properties

    public Long getId() { return id; }
    public Integer getVersion() { return version; }

    // Mutable properties

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Directory getDirectory() {
        return directory;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    public SortedSet<FeedEntry> getFeedEntries() {
        return feedEntries;
    }

    public void setFeedEntries(SortedSet<FeedEntry> feedEntries) {
        this.feedEntries = feedEntries;
    }

    public String toString() {
        return "Feed: " + getId();
    }
}
