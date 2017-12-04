/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.example.seamdiscs.test.graphene;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class tests artists management in SeamDiscs application
 *
 * @author Ondrej Skutka
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class SeamDiscsFunctionalTest extends SeamDiscsFunctionalTestBase {

    // TRINIDAD-2223 - A problem in Trinidad's ADF RequestQueue.js causes trouble in HTMLUnit and PhantomJS:
    // TypeError: Cannot call method "toLowerCase" of undefined (http://127.0.0.1:8080/seam-seamdiscs/adf/jsLibs/Common2_0_0_beta_2.js#6909)
    // form.enctype is the 'undefined' for some reason
    @Before
    public void assumeRealBrowser() {
        Assume.assumeTrue(isRealBrowser());
    }
    
    @Test
    @InSequence(1)
    public void createWithDiscsTest() {
        clickAndWaitHttp(MANAGE_ARTISTS);
        clickAndWaitHttp(CREATE_ARTIST_BUTTON);
        type(ARTIST_NAME, "Lou Reed");
        type(ARTIST_DESCRIPTION, "First came to prominence as the guitarist and principal singer-songwriter of The Velvet Underground. Than began a long and eclectic solo career.");
        createDisc("Metal Machine Music", "1975");
        createDisc("Sally Can't Dance", "1974");
        createDisc("Rock and Roll Heart", "1977");
        clickAndWaitHttp(ARTIST_PERSIST);
        assertTrue("Cannot create artist with discs", isTextInSource("Successfully created"));

        findAndClickArtist("Lou Reed");

        // check whether it is the Lou Reed
        assertEquals("This artist is not Lou Reed!", "Lou Reed", getValue(ARTIST_NAME));

        // check discs (they should be sorted by release date)
        checkDisc(1, "Sally Can't Dance", "1974", EMPTY_DISC_DESCRIPTION);
        checkDisc(2, "Metal Machine Music", "1975", EMPTY_DISC_DESCRIPTION);
        checkDisc(3, "Rock and Roll Heart", "1977", EMPTY_DISC_DESCRIPTION);
    }

    @Test
    @InSequence(2)
    public void paginationTest() {
        clickAndWaitHttp(MANAGE_ARTISTS);
        findAndClickArtist("Fairport Convention"); // should be on second page
        checkDisc(1, "Liege and Lief", "", "The first folk-rock album ever made in the UK, this was the only studio recording of the classic line up of Sandy Denny, Richard Thompson, Dave Swarbick, Ashley Hutchings and Simon Nicol");
    }
// TODO check creating empty disc

    @Test
    @InSequence(3)
    public void editDiscsTest() {
        clickAndWaitHttp(MANAGE_ARTISTS);
        // correct Rock and Roll Heart releas date
        findAndClickArtist("Lou Reed");
        type(getBy(ARTIST_NTH_DISC_YEAR, "last()"), "1976");

        clickAndWaitHttp(ARTIST_UPDATE);
        findAndClickArtist("Lou Reed");

        checkDisc(1, "Sally Can't Dance", "1974", EMPTY_DISC_DESCRIPTION);
        checkDisc(2, "Metal Machine Music", "1975", EMPTY_DISC_DESCRIPTION);
        checkDisc(3, "Rock and Roll Heart", "1976", EMPTY_DISC_DESCRIPTION); // this was altered 
    }

    @Test
    @InSequence(4)
    public void addBandTest() {
        clickAndWaitHttp(MANAGE_ARTISTS);
        clickAndWaitHttp(CREATE_BAND_BUTTON);
        type(ARTIST_NAME, "The Velvet Underground");
        type(ARTIST_DESCRIPTION, "An underground band.");
        createDisc("White Light/White Heat", "1968");
        createDisc("The Velvet Underground and Nico", "1967");

        addBandMember("Lou Reed");
        addBandMember("Sterling Morrison");
        addBandMember("John Cale");
        addBandMember("Maureen Tucker");
        addBandMember("Nico");

        clickAndWaitHttp(ARTIST_PERSIST);
        assertTrue("Cannot create artist with discs", isTextInSource("Successfully created"));

        findAndClickArtist("The Velvet Underground");

        // check whether it is the Lou Reed
        assertEquals("This artist is not the Velvet Underground!", "The Velvet Underground", getValue(ARTIST_NAME));

        // check discs (they should be sorted by release date)
        checkDisc(1, "The Velvet Underground and Nico", "1967", EMPTY_DISC_DESCRIPTION);
        checkDisc(2, "White Light/White Heat", "1968", EMPTY_DISC_DESCRIPTION);

        checkBandMember(1, "Lou Reed");
        checkBandMember(2, "Sterling Morrison");
        checkBandMember(3, "John Cale");
        checkBandMember(4, "Maureen Tucker");
        checkBandMember(5, "Nico");
    }
    
    @Test
    @InSequence(5)
    public void filterTest() {
        clickAndWaitHttp(MANAGE_ARTISTS);
        type(ARTIST_FILTER, "Fairport");
        sleep(3000);
        clickAndWaitHttp(ARTISTS_FIRST_ARTIST_LINK);
        checkDisc(1, "Liege and Lief", "", "The first folk-rock album ever made in the UK, this was the only studio recording of the classic line up of Sandy Denny, Richard Thompson, Dave Swarbick, Ashley Hutchings and Simon Nicol");
    }
    
    @Test
    @Ignore("bz975128")
    @InSequence(6)
    public void editDiscTest() {
        clickAndWaitHttp(MANAGE_DISCS);
        // correct Rock and Roll Heart description
        findAndClickDisc("Rock and Roll Heart"); // should not be on the first page
        checkDiscDetail("Rock and Roll Heart", "1976", "", "Lou Reed");

        type(DISC_DETAIL_DESCRIPTION, "A sensitive and revealing look into the prince of darkness.");

        clickAndWaitHttp(DISC_DETAIL_UPDATE);

        findAndClickDisc("Rock and Roll Heart");
        checkDiscDetail("Rock and Roll Heart", "1976", "A sensitive and revealing look into the prince of darkness.", "Lou Reed");

        // check whether it's ok from the artists' perspective
        clickAndWaitHttp(MANAGE_ARTISTS);

        findAndClickArtist("Lou Reed");

        // check whether it is the Lou Reed
        assertEquals("This artist is not Lou Reed!", "Lou Reed", getValue(ARTIST_NAME));

        // check discs (they should be sorted by release date)
        checkDisc(1, "Sally Can't Dance", "1974", EMPTY_DISC_DESCRIPTION);
        checkDisc(2, "Metal Machine Music", "1975", EMPTY_DISC_DESCRIPTION);
        checkDisc(3, "Rock and Roll Heart", "1976", "A sensitive and revealing look into the prince of darkness.");
    }

    @Test
    @Ignore("bz975128")
    @InSequence(7)
    public void discPaginationTest() {
        findAndClickDisc("Rock and Roll Heart"); // should not be on the first page
        checkDiscDetail("Rock and Roll Heart", "1976", "A sensitive and revealing look into the prince of darkness.", "Lou Reed");
    }

    @Test
    @Ignore("bz975128")
    @InSequence(8)
    public void addDiscTest() {
        clickAndWaitHttp(MANAGE_DISCS);
        clickAndWaitHttp(CREATE_DISC_BUTTON);
        type(DISC_DETAIL_TITLE, "Berlin");
        selectByValue(DISC_DETAIL_ARTIST, "Lou Reed");
        type(DISC_DETAIL_RELEASE_DATE, "1973");
        type(DISC_DETAIL_DESCRIPTION, "A tragic rock opera about a doomed couple that addresses themes of drug use and depression.");
        clickAndWaitHttp(DISC_DETAIL_PERSIST);
        assertTrue("Cannot create disc", isTextInSource("Successfully created"));

        // check whether it's ok from the artists' perspective
        clickAndWaitHttp(MANAGE_ARTISTS);

        findAndClickArtist("Lou Reed");

        // check whether it is the Lou Reed
        assertEquals("This artist is not Lou Reed!", "Lou Reed", getValue(ARTIST_NAME));

        // check discs (they should be sorted by release date)
        checkDisc(1, "Berlin", "1973", "A tragic rock opera about a doomed couple that addresses themes of drug use and depression.");
        checkDisc(2, "Sally Can't Dance", "1974", EMPTY_DISC_DESCRIPTION);
        checkDisc(3, "Metal Machine Music", "1975", EMPTY_DISC_DESCRIPTION);
        checkDisc(4, "Rock and Roll Heart", "1976", "A sensitive and revealing look into the prince of darkness.");
    }

    @Test
    @Ignore("bz975128")
    @InSequence(9)
    public void removeDiscTest() {
        clickAndWaitHttp(MANAGE_DISCS);
        // correct Rock and Roll Heart description
        findAndClickDisc("Berlin");
        checkDiscDetail("Berlin", "1973", "A tragic rock opera about a doomed couple that addresses themes of drug use and depression.", "Lou Reed");

        clickAndWaitHttp(DISC_DETAIL_REMOVE);

        // check whether it's ok from the artists' perspective
        clickAndWaitHttp(MANAGE_ARTISTS);

        findAndClickArtist("Lou Reed");

        // check whether it is the Lou Reed
        assertEquals("This artist is not Lou Reed!", "Lou Reed", getValue(ARTIST_NAME));

        // check discs (they should be sorted by release date)
        checkDisc(1, "Sally Can't Dance", "1974", EMPTY_DISC_DESCRIPTION);
        checkDisc(2, "Metal Machine Music", "1975", EMPTY_DISC_DESCRIPTION);
        checkDisc(3, "Rock and Roll Heart", "1976", "A sensitive and revealing look into the prince of darkness.");
    }

    @Test
    @Ignore("bz975128")
    @InSequence(10)
    public void cancelDiscTest() {
        clickAndWaitHttp(MANAGE_DISCS);
        // correct Rock and Roll Heart description
        findAndClickDisc("Rock and Roll Heart"); // should not be on the first page
        checkDiscDetail("Rock and Roll Heart", "1976", "A sensitive and revealing look into the prince of darkness.", "Lou Reed");

        type(DISC_DETAIL_DESCRIPTION, "Pretty lame album.");

        clickAndWaitHttp(DISC_DETAIL_CANCEL);

        findAndClickDisc("Rock and Roll Heart");
        checkDiscDetail("Rock and Roll Heart", "1976", "A sensitive and revealing look into the prince of darkness.", "Lou Reed");

        // check whether it's ok from the artists' perspective
        clickAndWaitHttp(MANAGE_ARTISTS);

        findAndClickArtist("Lou Reed");

        // check whether it is the Lou Reed
        assertEquals("This artist is not Lou Reed!", "Lou Reed", getValue(ARTIST_NAME));

        // check discs (they should be sorted by release date)
        checkDisc(1, "Sally Can't Dance", "1974", EMPTY_DISC_DESCRIPTION);
        checkDisc(2, "Metal Machine Music", "1975", EMPTY_DISC_DESCRIPTION);
        checkDisc(3, "Rock and Roll Heart", "1976", "A sensitive and revealing look into the prince of darkness.");
        
    }
}
