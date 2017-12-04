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
package org.jboss.seam.example.tasks.test.graphene;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import static org.jboss.arquillian.graphene.Graphene.*;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.seam.example.common.test.DeploymentResolver;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

/**
 * This is the base class for Tasks functional tests. Uses jQuery library and
 * Selenium to match AJAX updates.
 *
 * @author kpiwko
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class TasksFunctionalTest extends SeamGrapheneTest {

    public static final String LOGIN_URL = "login.seam";
    public static final String TASKS_URL = "tasks.seam";
    public static final String LOGIN_USERNAME = "id=login:username";
    public static final String LOGIN_PASSWORD = "id=login:password";
    public static final String LOGIN_SUBMIT = "xpath=//input[@value='Login']";
    public static final String ACTION_BUTTON_FORMATTER = "xpath=//td[contains(., '%s')]/ancestor::tr/descendant::img[@title='%s']";
    public static final String RESOLVE_BTN_TITLE = "Resolve this task";
    public static final String EDIT_BTN_TITLE = "Edit this task";
    public static final String DELETE_BTN_TITLE = "Delete this task";
    public static final String UNDO_BTN_TITLE = "Undo this task";
    public static final String DELETE_BTN_CAT_TITLE = "Delete this category";
    public static final String DEFAULT_USERNAME = "demo";
    public static final String DEFAULT_PASSWORD = "demo";
    public static final String TASKS_LINK = "xpath=//a[.='Tasks']";
    public static final String RESOLVED_LINK = "xpath=//a[.='Resolved tasks']";
    public static final String CATEGORIES_LINK = "xpath=//a[.='Categories']";
    public static final String LOGOUT_LINK = "id=menuLogoutId";
    public static final String EDIT_TASK_DESCRIPTION = "xpath=//form[@id='updateTask']/input[@class='nameField']";
    public static final String EDIT_TASK_CATEGORY = "xpath=//form[@id='updateTask']/select[@id='editTaskCategory']";
    public static final String EDIT_TASK_SUBMIT = "xpath=//form[@id='updateTask']/input[@id='update']";
    public static final String NEW_TASK_DESCRIPTION = "xpath=//form[@id='newTask']/input[@id='editTaskName']";
    public static final String NEW_TASK_CATEGORY = "xpath=//form[@id='newTask']/select[@id='editTaskCategory']";
    public static final String NEW_TASK_SUBMIT = "xpath=//form[@id='newTask']/input[@id='editTaskSubmit']";
    public static final String NEW_CATEGORY_DESCRIPTION = "xpath=//form[@id='newCategoryForm']/input[@id='editCategoryName']";
    public static final String NEW_CATEGORY_SUBMIT = "xpath=//form[@id='newCategoryForm']/input[@id='editCategorySubmit']";
    public static final String CATEGORIES_PRESENT = "xpath=//table[@id='categories']/tbody/tr";
    public static final String TASKS_PRESENT = "xpath=//table[@id='tasks']/tbody/tr";

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return DeploymentResolver.createDeployment();
    }

    @Before
    @Override
    public void beforeTest() {
        // Graphene request guards act strange with HTMLUnit
        Assume.assumeTrue(isRealBrowser());
        open(contextPath + LOGIN_URL);
        type(getBy(LOGIN_USERNAME), DEFAULT_USERNAME);
        type(getBy(LOGIN_PASSWORD), DEFAULT_PASSWORD);
        click(getBy(LOGIN_SUBMIT));
        sleep(2000);// ugly, but need to wait until the table is fully (ajax) loaded
        waitModel().until().element(getBy(CATEGORIES_PRESENT)).is().present();
        
        assertTrue("Navigation failure. Tasks page expected.", browser.getCurrentUrl().contains(TASKS_URL));
    }

    @Test
    @InSequence(1)
    public void resolveTuringTask() {
        String turing = "Build the Turing machine";
        resolveTask(turing);
        buttonMissing(turing, RESOLVE_BTN_TITLE);
        click(getBy(RESOLVED_LINK));
        sleep(2000);// ugly, but need to wait until the table is fully (ajax) loaded
        waitModel().until().element(getBy(TASKS_PRESENT)).is().present();

        buttonPresent(turing, UNDO_BTN_TITLE);
        clickAndWaitHttp(getBy(LOGOUT_LINK));
    }

    @Test
    @InSequence(2)
    public void deleteMilkTask() {
        String milk = "Buy milk";
        deleteTask(milk);
        buttonMissing(milk, RESOLVE_BTN_TITLE);
        click(getBy(RESOLVED_LINK));
        sleep(2000);// ugly, but need to wait until the table is fully (ajax) loaded
        waitModel().until().element(getBy(TASKS_PRESENT)).is().present();

        buttonMissing(milk, UNDO_BTN_TITLE);
        clickAndWaitHttp(getBy(LOGOUT_LINK));
    }

    @Test
    @InSequence(3)
    public void undoTurtleTask() {
        String turtle = "Buy a turtle";
        click(getBy(RESOLVED_LINK));
        sleep(2000);// ugly, but need to wait until the table is fully (ajax) loaded
        waitModel().until().element(getBy(TASKS_PRESENT)).is().present();

        undoTask(turtle);
        buttonMissing(turtle, UNDO_BTN_TITLE);

        click(getBy(TASKS_LINK));
        sleep(2000);// ugly, but need to wait until the table is fully (ajax) loaded
        waitModel().until().element(getBy(CATEGORIES_PRESENT)).is().present();

        buttonPresent(turtle, RESOLVE_BTN_TITLE);
        clickAndWaitHttp(getBy(LOGOUT_LINK));
    }

    @Test
    @InSequence(4)
    public void editTurtleTask() {
        String turtle = "Buy a turtle";
        String newCategory = "Work";
        String newDescription = "Buy a turtle and take it to work";
        editTask(turtle, newCategory, newDescription);
        buttonPresent(newDescription, RESOLVE_BTN_TITLE);
        clickAndWaitHttp(getBy(LOGOUT_LINK));
    }

    @Test
    @InSequence(5)
    public void createQACategory() {
        String category = "JBoss QA";
        click(getBy(CATEGORIES_LINK));
        sleep(2000);// ugly, but need to wait until the table is fully (ajax) loaded
        waitModel().until().element(getBy(CATEGORIES_PRESENT)).is().present();

        newCategory(category);
        buttonPresent(category, DELETE_BTN_CAT_TITLE);
        clickAndWaitHttp(getBy(LOGOUT_LINK));
    }

    @Test
    @InSequence(6)
    public void createSeleniumTask() {
        String description = "Create selenium ftests for all available examples";
        newTask("Work", description);
        buttonPresent(description, RESOLVE_BTN_TITLE);
        clickAndWaitHttp(getBy(LOGOUT_LINK));
    }

    @Test
    @InSequence(7)
    public void deleteSchoolCategory() {
        String category = "School";
        click(getBy(CATEGORIES_LINK));
        sleep(2000);// ugly, but need to wait until the table is fully (ajax) loaded
        waitModel().until().element(getBy(CATEGORIES_PRESENT)).is().present();

        buttonPress(category, DELETE_BTN_CAT_TITLE);
        By by = getBy(String.format(ACTION_BUTTON_FORMATTER, category, DELETE_BTN_CAT_TITLE));
        waitModel().until().element(by).is().not().present();
        buttonMissing(category, DELETE_BTN_CAT_TITLE);

        // all tasks from category are deleted as well
        click(getBy(TASKS_LINK));
        sleep(2000);// ugly, but need to wait until the table is fully (ajax) loaded
        buttonMissing("Finish the RESTEasy-Seam integration example", RESOLVE_BTN_TITLE);
        clickAndWaitHttp(getBy(LOGOUT_LINK));
    }

    /**
     * Presses undo button for given task
     *
     * @param task The task name
     */
    protected void undoTask(String task) {
        By by = buttonPress(task, UNDO_BTN_TITLE);
        waitModel().until().element(by).is().not().present();
    }

    /**
     * Presses resolve button for given task
     *
     * @param task The task name
     */
    protected void resolveTask(String task) {
        By by = buttonPress(task, RESOLVE_BTN_TITLE);
        waitModel().until().element(by).is().not().present();
    }

    /**
     * Presses delete button for given task
     *
     * @param task The task name
     */
    protected void deleteTask(String task) {
        By by = buttonPress(task, DELETE_BTN_TITLE);
        waitModel().until().element(by).is().not().present();
    }

    /**
     * Executes arbitrary button task, e.g. delete, undo, resolve
     *
     * @param description Name of task or description on which an action is
     * triggered
     * @param button Type of action to be triggered - title of the button
     */
    protected By buttonPress(String description, String button) {
        By btn = buttonPresent(description, button);
        clickAndWaitAjax(btn);
        return btn;
    }

    /**
     * Checks whether button is present on page
     *
     * @param description The task/category associated with button
     * @param button Button type label
     * @return Button locator
     */
    protected By buttonPresent(String description, String button) {
        String btnStr = String.format(ACTION_BUTTON_FORMATTER, description, button);
        By btn = getBy(btnStr);
        waitModel().until().element(btn).is().present();
        assertTrue("There should be a '" + button + "' button for: " + description + ".", isElementPresent(btn));
        return btn;
    }

    /**
     * Checks whether button is not present on page
     *
     * @param description The task/category associated with button
     * @param button Button type label
     * @return Button locator
     */
    protected By buttonMissing(String task, String button) {
        String btnStr = String.format(ACTION_BUTTON_FORMATTER, task, button);
        By btn = getBy(btnStr);
        assertFalse("There should NOT be a '" + button + "' button for: " + task + ".", isElementPresent(btn));
        return btn;
    }

    /**
     * Creates new task
     *
     * @param category Category of the task
     * @param description Description of the task
     */
    protected void newTask(String category, String description) {
        selectByValue(getBy(NEW_TASK_CATEGORY), category);
        type(getBy(NEW_TASK_DESCRIPTION), description);
        clickAndWaitAjax(getBy(NEW_TASK_SUBMIT));
        By by = getBy(String.format(ACTION_BUTTON_FORMATTER, description, RESOLVE_BTN_TITLE));
        waitModel().until().element(by).is().present();

    }

    /**
     * Edits task
     *
     * @param task Old description of the task
     * @param newCategory New category
     * @param newDescription Old description of the task
     */
    protected void editTask(String task, String newCategory, String newDescription) {
        By btn = buttonPresent(task, EDIT_BTN_TITLE);
        click(btn);
        selectByValue(getBy(EDIT_TASK_CATEGORY), newCategory);
        type(getBy(EDIT_TASK_DESCRIPTION), newDescription);
        clickAndWaitAjax(getBy(EDIT_TASK_SUBMIT));

        By by = getBy(String.format(ACTION_BUTTON_FORMATTER, newDescription, RESOLVE_BTN_TITLE));
        waitModel().until().element(by).is().present();

    }

    /**
     * x
     * Creates new category
     *
     * @param category Category description
     */
    protected void newCategory(String category) {
        type(getBy(NEW_CATEGORY_DESCRIPTION), category);
        clickAndWaitAjax(getBy(NEW_CATEGORY_SUBMIT));
        By by = getBy(String.format(ACTION_BUTTON_FORMATTER, category, DELETE_BTN_CAT_TITLE));
        waitModel().until().element(by).is().present();
    }
}
