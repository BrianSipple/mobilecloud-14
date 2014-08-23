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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.collect.Lists;

@Controller
public class VideoServiceCtrl {

	@Autowired
	VideoRepository videos;

	Video video = new Video();

	public static final String TITLE_PARAMETER = "title";
	public static final String DURATION_PARAMETER = "duration";

	/*
	 * - Returns the list of videos that have been added to the server as JSON.
	 * The list of videos should be persisted using Spring Data. The list of
	 * Video objects should be able to be unmarshalled by the client into a
	 * Collection<Video>. - The return content-type should be application/json,
	 * which will be the default if you use @ResponseBody
	 */
	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH, method = RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideoList() {
		return Lists.newArrayList(videos.findAll());
	}

	/*
	 * - The video metadata is provided as an application/json request body. The
	 * JSON should generate a valid instance of the Video class when
	 * deserialized by Spring's default Jackson library. - Returns the JSON
	 * representation of the Video object that was stored along with any updates
	 * to that object made by the server. - **_The server should store the Video
	 * in a Spring Data JPA repository. If done properly, the repository should
	 * handle generating ID's._** - A video should not have any likes when it is
	 * initially created. - You will need to add one or more annotations to the
	 * Video object in order for it to be persisted with JPA.
	 */
	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH, method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody Video addVideo(@RequestBody Video v) {
	Video video = videos.save(v);
	String url = this.getDataUrl(video.getId());
	video.setUrl(url);
	return video;
	}
	
	


	private String getUrlBaseForLocalServer() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String base = "http://" + request.getServerName()
				+ ((request.getServerPort() != 80) ? ":" + request.getServerPort() : "");
		return base;
	}

	private String getDataUrl(long videoId) {
		String url = getUrlBaseForLocalServer() + "/video/" + videoId + "/data";
		return url;
	}

	/*
	 * Returns the video with the given id or 404 if the video is not found.
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}", method=RequestMethod.GET)
	public @ResponseBody Video getVideoById(@PathVariable("id") long id, HttpServletResponse response){
		Video video = null;
		try {
			video = videos.findOne(id);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return video;
		
	}

	/*
	 * - Allows a user to like a video. Returns 200 Ok on success, 404 if the
	 * video is not found, or 400 if the user has already liked the video. - The
	 * service should should keep track of which users have liked a video and
	 * prevent a user from liking a video twice. A POJO Video object is provided
	 * for you and you will need to annotate and/or add to it in order to make
	 * it persistable. - A user is only allowed to like a video once. If a user
	 * tries to like a video a second time, the operation should fail and return
	 * 400 Bad Request.
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}/like", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody void likeVideo(@PathVariable("id") long id, HttpServletResponse response, Principal user) throws IOException  {

		if (!videos.exists(id)) {
				response.sendError(404);
				return;
		}
		String username = user.getName();
		Video v = videos.findOne(id);
		Set<String> likesUsernames = v.getLikesUsernames();
		List<String> happyUsers = v.getLikedVideo();
		// Checks if the user has already liked the video.
		if (likesUsernames.contains(username)) {
				response.sendError(400);
				return;
		} else {
			long likes = v.getLikes();
			v.setLikes( ++likes);
			v.getLikesUsernames().add(username);
		}
		if (!happyUsers.contains(username)) {
			happyUsers.add(username);
			v.setLikedVideo(happyUsers);
		}
		videos.save(v);
	}

	/*
	 * - Allows a user to unlike a video that he/she previously liked. Returns
	 * 200 OK on success, 404 if the video is not found, and a 400 if the user
	 * has not previously liked the specified video.
	 */
	@RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}/unlike", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody void unlikeVideo(@PathVariable("id") long id, HttpServletResponse response, Principal user) throws IOException {

		if (!videos.exists(id)) {
				response.sendError(404);
				return;
			} 
		String username = user.getName();
		Video v = videos.findOne(id);
		Set<String> unlikesUsernames = v.getUnlikesUsernames();
		List<String> happyUsers = v.getLikedVideo();
		// Checks if the user has already liked the video.
		if (unlikesUsernames.contains(username)) {
				response.sendError(400);
				return;
		} else {
			long likes = v.getLikes();
			v.setLikes( --likes);
			v.getUnlikesUsernames().add(username);
		}
		if (happyUsers.contains(username)) {
			happyUsers.remove(username);
			v.setLikedVideo(happyUsers);
		}
		videos.save(v);
	}
	

	/*
	 * - Returns a list of the string usernames of the users that have liked the
	 * specified video. If the video is not found, a 404 error should be
	 * generated.
	 */
	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH + "/{id}/likedby", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public  @ResponseBody Collection<String> getUsersWhoLikedVideo
	(@PathVariable("id") long id, HttpServletResponse response) throws IOException {
		Collection<String> happyUsers = new ArrayList<String>();
		if (videos.exists(id)) {
			Video v = videos.findOne(id);
			happyUsers = v.getLikedVideo();
		} else {
			response.sendError(404);			
		}
		
		return happyUsers;
	}

	/*
	 * - Returns a list of videos whose titles match the given parameter or an
	 * empty list if none are found.
	 */
	@RequestMapping(value = VideoSvcApi.VIDEO_TITLE_SEARCH_PATH, method = RequestMethod.GET)
	public @ResponseBody Collection<Video> findByTitle(
	// Tell Spring to use the "title" parameter in the HTTP request's query
	// string as the value for the title method parameter
			@RequestParam(TITLE_PARAMETER) String title) {
		return videos.findByName(title);
	}

	/*
	 * - Returns a list of videos whose durations are less than the given
	 * parameter or an empty list if none are found.
	 */
	@RequestMapping(value = VideoSvcApi.VIDEO_DURATION_SEARCH_PATH, method = RequestMethod.GET)
	public @ResponseBody Collection<Video> findByDurationLessThan(
			@RequestParam(DURATION_PARAMETER) long duration) {
		return videos.findByDurationLessThan(duration);
		
	}
	

}