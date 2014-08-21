/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.magnum.mobilecloud.video;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javassist.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.magnum.mobilecloud.video.client.VideoSvcApi;
import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.collect.Lists;

@Controller
public class VideoServiceCtrl {

	/**
	 * You will need to create one or more Spring controllers to fulfill the
	 * requirements of the assignment. If you use this file, please rename it
	 * to something other than "AnEmptyController"
	 * 
	 * 
		 ________  ________  ________  ________          ___       ___  ___  ________  ___  __       
		|\   ____\|\   __  \|\   __  \|\   ___ \        |\  \     |\  \|\  \|\   ____\|\  \|\  \     
		\ \  \___|\ \  \|\  \ \  \|\  \ \  \_|\ \       \ \  \    \ \  \\\  \ \  \___|\ \  \/  /|_   
		 \ \  \  __\ \  \\\  \ \  \\\  \ \  \ \\ \       \ \  \    \ \  \\\  \ \  \    \ \   ___  \  
		  \ \  \|\  \ \  \\\  \ \  \\\  \ \  \_\\ \       \ \  \____\ \  \\\  \ \  \____\ \  \\ \  \ 
		   \ \_______\ \_______\ \_______\ \_______\       \ \_______\ \_______\ \_______\ \__\\ \__\
		    \|_______|\|_______|\|_______|\|_______|        \|_______|\|_______|\|_______|\|__| \|__|

	 * 
	 */

	public static final String TITLE_PARAMETER = "title";
	public static final String DURATION_PARAMETER = "duration";

	public static final String VIDEO_404_MSG = "The video could not be found";
	public static final String ALREADY_LIKED_ERROR_MSG = "Sorry, you can only \"like\" a video once";
	public static final String NOT_YET_LIKED_ERROR_MSG = "Sorry, you cannont \"UN\"like without \"liking\" first";
	public static final String VIDEOS_BY_NAME_ERROR_MSG = "No videos were found matching that title";
	public static final String VIDEOS_BY_DURATION_ERROR_MSG = "No videos were found with a length below that duration";





	// Wire up the JPA repository interface we created for videos 
	@Autowired
	private VideoRepository videos;

	private Video mVideo = new Video();


	@RequestMapping(value=VideoSvcApi.TOKEN_PATH, method=RequestMethod.POST)
	public @ResponseBody String goodLuck(){
		return "Good Luck!";
	}


	/**
	 * GET /video
	 * - Returns the list of videos that have been added to the
	 * server as JSON. The list of videos should be persisted
	 * using Spring Data. The list of Video objects should be able 
	 * to be unmarshalled by the client into a Collection<Video>.
	 *  - The return content-type should be application/json, which
	 * will be the default if you use @ResponseBody
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideoList(Principal p) {

		return Lists.newArrayList(videos.findAll());
	}

	/**
	 * POST /video
	 *  - The video metadata is provided as an application/json request
	 *    body. The JSON should generate a valid instance of the 
	 *    Video class when deserialized by Spring's default 
	 *    Jackson library.
	 *  - Returns the JSON representation of the Video object that
	 *    was stored along with any updates to that object made by the server. 
	 *  - **_The server should store the Video in a Spring Data JPA repository.
	 *  	 If done properly, the repository should handle generating ID's._** 
	 *  - A video should not have any likes when it is initially created.
	 *  - You will need to add one or more annotations to the Video object
	 *    in order for it to be persisted with JPA.
	 */    
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody Video addVideo(@RequestBody Video v, Principal p) {

		mVideo = videos.save(v);
		String url = this.getDataUrl(mVideo.getId());
		mVideo.setUrl(url);
		return mVideo;
	}


	/**
	 * GET /video/{id}
	 *  - Returns the video with the given id or 404 if the video is not found.
	 */  
	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH + "/{id]", method = RequestMethod.GET)
	public @ResponseBody Video getVideoById(
			@PathVariable("id") long id,
			HttpServletResponse response) throws NotFoundException {

		try {
			mVideo = videos.findOne(id);
		} catch (Throwable throwable) {
			response.sendError(404, VIDEO_404_MSG);
		} finally {
			return mVideo;
		}
	}

	/**
	 * POST /video/{id}/like
	 *  - Allows a user to like a video. Returns 200 Ok on success, 404 if the
	 *    video is not found, or 400 if the user has already liked the video.
	 *  - The service should should keep track of which users have liked a video and
	 *    prevent a user from liking a video twice. A POJO Video object is provided for 
	 *    you and you will need to annotate and/or add to it in order to make it persistable.
	 *  - A user is only allowed to like a video once. If a user tries to like a video
	 *     a second time, the operation should fail and return 400 Bad Request.
	 */     
	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH + "/{id}/like", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void addLike(
			@PathVariable("id") long id,
			Principal p,
			HttpServletResponse response) throws NotFoundException, IOException {

		if(!videos.exists(id)) {
			response.sendError(404, VIDEO_404_MSG);
			return;
		}

		String username = p.getName();
		mVideo = videos.findOne(id);
		Set<String> usersThatLiked = mVideo.getUsersThatLiked();

		// Checks whether the user has already liked the video, throwing a 400 error if so
		if (usersThatLiked.contains(username)) {
			response.sendError(400, ALREADY_LIKED_ERROR_MSG);
			return;
		} 
		
		usersThatLiked.add(username);  // add username to the set...
		mVideo.setUsersThatLiked(usersThatLiked); // ...then set the set on the video...
		long likeCount = mVideo.getLikes();		// ... and of course remember to adjust the like count!
		mVideo.setLikes(++likeCount);
		
		videos.save(mVideo);	// save our changes!
	}

	
	/**
	 * POST /video/{id}/unlike
	 *  - Allows a user to unlike a video that he/she previously liked. Returns 200 OK
	 *     on success, 404 if the video is not found, and a 400 if the user has not 
	 *     previously liked the specified video.
	 */  
	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH + "/{id}/unlike", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void addUnlike(
			@PathVariable("id") long id,
			Principal p,
			HttpServletResponse response) throws NotFoundException, IOException {

		if (!videos.exists(id)) {
			response.sendError(404, VIDEO_404_MSG);
			return;
		}
		
		mVideo = videos.findOne(id);
		String username = p.getName();
		Set<String> usersThatLiked = mVideo.getUsersThatLiked();
	
		// One does not simply "unlike" without first "likeing"
		if (!usersThatLiked.contains(username)) {
			response.sendError(400, NOT_YET_LIKED_ERROR_MSG);
			return;
		}
		
		usersThatLiked.remove(username);			// remove username from the name set...
		mVideo.setUsersThatLiked(usersThatLiked);  // then set the set on the video...
		long likeCount = mVideo.getLikes();       // then upate the video's like count
		mVideo.setLikes(--likeCount);
		
		videos.save(mVideo);
	}
	
	
	/**
	 * GET /video/{id}/likedby
	 * - Returns a list of the string usernames of the users that have liked the specified
	 *    video. If the video is not found, a 404 error should be generated.
	 * @throws IOException 
	 */
	@SuppressWarnings("finally")
	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH + "/{id}/likedby", method = RequestMethod.GET)
	public Set<String> getUsersThatLiked(
			@PathVariable("id") long id,
			HttpServletResponse response) throws NotFoundException, IOException {
	
		Video video = null;
		Set<String> usersThatLiked = null;
	
		try {
			video = videos.findOne(id);
			usersThatLiked = video.getUsersThatLiked();
		} catch (Throwable throwable) {
			response.sendError(404, VIDEO_404_MSG);
		} finally {
			return usersThatLiked;
		}
	}
	
	
	/**
	 *	GET /video/search/findByName?title={title}
	 *	- Returns a list of videos whose titles match the given parameter or an empty
	 *	  list if none are found.
	 */
	@RequestMapping(value = VideoSvcApi.VIDEO_TITLE_SEARCH_PATH, method = RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideosByTitle(
			@PathVariable(TITLE_PARAMETER) String title,
			HttpServletResponse response) throws IOException {
	
		Collection<Video> videosByName = null;
	
		try {
			videosByName = videos.findByName(title);
		} catch (Throwable throwable) {
			response.sendError(404, VIDEOS_BY_NAME_ERROR_MSG);
		} finally {
			return videosByName;
		}	
	}
	
	/**
	 * GET /video/search/findByDurationLessThan?duration={duration}
	 * 
	 * - Returns a list of videos whose durations are less than the given parameter or
	 *   an empty list if none are found.
	 * @throws IOException 
	 */
	@RequestMapping(value = VideoSvcApi.VIDEO_DURATION_SEARCH_PATH, method = RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideosByMaxDuration(
			@PathVariable(DURATION_PARAMETER) long maxDuration,
			HttpServletResponse response) throws IOException {
	
		Collection<Video> videosByMaxDuration = null;
	
		try {
			videosByMaxDuration = videos.findByDurationLessThan(maxDuration);
		} catch (Throwable throwable) {
			response.sendError(404, VIDEOS_BY_DURATION_ERROR_MSG);
		} finally {
			return videosByMaxDuration;
		}
	}



	//////////////////////////////// HELPERS ////////////////////////////////
	
	/**
	 * Dynamically generate the url base for a local server
	 */
	private String getUrlBaseForLocalServer() {
	
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
	
		String urlBase = "http://" + request.getServerName()  + 
				((request.getServerPort() != 80) ? ":" + request.getServerPort() : "");
	
		return urlBase;
	}
	
	
	/**
	 * Generate the data URL of a video received in a POST request
	 * by inserting its id into the path
	 */
	private String getDataUrl(long videoId) {
		String url = getUrlBaseForLocalServer() + "/video/" + id + "/data";
		return url;
	}
}
