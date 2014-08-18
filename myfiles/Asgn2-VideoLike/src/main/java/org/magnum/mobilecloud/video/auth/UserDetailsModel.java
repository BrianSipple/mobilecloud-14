package org.magnum.mobilecloud.video.auth;

/**
 * Defines the User objects that we will register when configuring
 * OAuth for this application
 * @author BrianSipple
 *
 */
public class UserDetailsModel {

	static final User ADMIN = (User) User.create("admin", "pass", "USER", "ADMIN");
	static final User USER0 = (User) User.create("user0", "pass", "USER");
	
}
