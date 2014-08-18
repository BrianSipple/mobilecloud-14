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

import java.security.Principal;
import java.util.Collection;

import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
	
	public static final String VIDEO_SVC_PATH = "/video";
	public static final String VIDEO_ID_PATH = VIDEO_SVC_PATH + "{id}";
	public static final String VIDEO_LIKE_PATH = VIDEO_ID_PATH + "/like";
	public static final String VIDEO_UNLIKE_PATH = VIDEO_ID_PATH + "/unlike";
	public static final String VIDEO_LIKEDBY_PATH = VIDEO_ID_PATH + "/likedby";
	
	public static final String TOKEN_PATH = "/oauth/token";
	public static final String VIDEO_TITLE_SEARCH_PATH = VIDEO_SVC_PATH + "/search/findByName";
	public static final String VIDEO_DURATION_SEARCH_PATH = VIDEO_SVC_PATH + "/search/findByDuration";
	
	public static final String ID_PARAMETER = "id";
	public static final String USERNAME_PARAMETER = "username";
	public static final String PASSWORD_PARAMETER = "password";
	public static final String DURATION_PARAMETER = "duration";
	public static final String TITLE_PARAMETER = "title";
	
	
	// Wire up the JPA repository interface we created for videos 
	@Autowired
	private VideoRepository videos;
	
	
	@RequestMapping(value=TOKEN_PATH, method=RequestMethod.POST)
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
	@RequestMapping(value=VIDEO_SVC_PATH, method=RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideos(Principal p) {
		
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
	@RequestMapping(value=VIDEO_SVC_PATH, method=RequestMethod.POST)
	public @ResponseBody Video addVideo(
			@RequestBody Video v) {
		videos.save(v);
		return v;
	}
	
	
	@RequestMapping(value = VIDEO_ID_PATH, method = RequestMethod.GET)
	public @ResponseBody Video getVideo(
			@RequestParam(ID_PARAMETER) long id) {
		
		return videos.findOne(id);
	}
	
	
	@RequestMapping(value = VIDEO_LIKE_PATH, method = RequestMethod.POST)
	public boolean addLike(
			@RequestParam(ID_PARAMETER) String id) {
		
	}
	
	
	@RequestMapping(value = VIDEO_UNLIKE_PATH, method = RequestMethod.POST)
	public ... (
			@RequestParam(ID_PARAMETER) String id) {
		
	}
	
	
	@RequestMapping(value = VIDEO_LIKEDBY_PATH, method = RequestMethod.GET)
	public ...(
			@RequestParam(ID_PARAMETER) String id) {
		
	}
	
	
	@RequestMapping(value = VIDEO_TITLE_SEARCH_PATH, method = RequestMethod.GET)
	public @ResponseBody Video getVideoByTitle() {
		
	}
	
	/**
	 * GET /video/search/findByDurationLessThan?duration={duration}
     * 
     * - Returns a list of videos whose durations are less than the given parameter or
     *   an empty list if none are found.
	 */
	@RequestMapping(value = VIDEO_DURATION_SEARCH_PATH, method = RequestMethod.GET)
	public @ResponseBody Video getVideoByDuration(
			@PathVariable(DURATION_PARAMETER) int duration) {
		
	}
	
	
	
	
	
}
