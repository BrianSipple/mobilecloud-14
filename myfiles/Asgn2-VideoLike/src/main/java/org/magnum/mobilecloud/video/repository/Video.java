	package org.magnum.mobilecloud.video.repository;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.common.base.Objects;

/**
 * A simple object to represent a video and its URL for viewing.
 * 
 * You probably need to, at a minimum, add some annotations to this
 * class.
 * 
 * You are free to add annotations, members, and methods to this
 * class. However, you probably should not change the existing
 * methods or member variables. If you do change them, you need
 * to make sure that they are serialized into JSON in a way that
 * matches what is expected by the auto-grader.
 * 
 * @author mitchell
 */

@Entity
public class Video {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String name;
	private String url;
	private long duration;
	
	private long likes;
	
	
	/**
	 * A String List of usernames corresponding to users that liked a video
	 */
	@ElementCollection(fetch = FetchType.EAGER, targetClass = org.magnum.mobilecloud.video.repository.Video.class)
	private Set<String> usersThatLiked = new HashSet<String>();
	
	public Video() {
	}

	public Video(String name, String url, long duration, long likes, Set<String> usersThatLiked) {
		super();
		this.name = name;
		this.url = url;
		this.duration = duration;
		this.likes = likes;
		this.usersThatLiked = usersThatLiked;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getLikes() {
		return likes;
	}
	
	public void setLikes(long likes) {
		this.likes = likes;
	}
	
	
	public Set<String> getUsersThatLiked() {
		return usersThatLiked;
	}
	
	public void setUsersThatLiked(Set<String> usernames) {
		this.usersThatLiked = usernames;
	}
	
	// More-granular add/remove methods for the usersThatLiked set
	public void addUserThatLiked(String username) {
		this.usersThatLiked.add(username);
	}

	
	public void removeUserThatLiked(String username) {
		if (this.usersThatLiked.contains(username)) {
			this.usersThatLiked.remove(username);
			this.likes -= 1;
		}
	}

	
	/**
	 * Two Videos will generate the same hashcode if they have exactly the same
	 * values for their name, url, and duration.
	 * 
	 */
	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(name, url, duration);
	}

	/**
	 * Two Videos are considered equal if they have exactly the same values for
	 * their name, url, and duration.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Video) {
			Video other = (Video) obj;
			// Google Guava provides great utilities for equals too!
			return Objects.equal(name, other.name)
					&& Objects.equal(url, other.url)
					&& duration == other.duration;
		} else {
			return false;
		}
	}

}
