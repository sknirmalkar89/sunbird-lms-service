/**
 * 
 */
package controllers.coursemanagement;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.sunbird.common.models.util.ActorOperations;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.LogHelper;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.request.ExecutionContext;
import org.sunbird.common.request.HeaderParam;
import org.sunbird.common.request.Request;
import org.sunbird.common.request.RequestValidator;

import com.fasterxml.jackson.databind.JsonNode;

import akka.util.Timeout;
import controllers.BaseController;
import play.libs.F.Promise;
import play.mvc.Result;

/**
 * This controller will handle all the api
 * related to course , add course , published course,
 * update course , search course.
 * @author Manzarul
 * @author Amit Kumar
 */
public class CourseController  extends BaseController{
	
private LogHelper logger = LogHelper.getInstance(CourseController.class.getName());
	
	/**
	 * This method will add a new course entry into cassandra db.
	 * @return Promise<Result>
	 */
	public Promise<Result> createCourse() {
		try {
			JsonNode requestData = request().body().asJson();
			logger.info("add new course data=" + requestData);
			ProjectLogger.log("add new course data=" + requestData, LoggerEnum.INFO.name());
			Request reqObj = (Request) mapper.RequestMapper.mapRequest(requestData, Request.class);
			RequestValidator.validateAddCourse(reqObj);
			reqObj.setOperation(ActorOperations.CREATE_COURSE.getValue());
	        reqObj.setRequest_id(ExecutionContext.getRequestId());
	        reqObj.setEnv(getEnvironment());
			HashMap<String, Object> innerMap = new HashMap<>();
			innerMap.put(JsonKey.COURSE, reqObj.getRequest());
			innerMap.put(JsonKey.REQUESTED_BY,getUserIdByAuthToken(request().getHeader(HeaderParam.X_Authenticated_Userid.getName())));
			reqObj.setRequest(innerMap);
			Timeout timeout = new Timeout(Akka_wait_time, TimeUnit.SECONDS);
			Promise<Result> res = actorResponseHandler(getRemoteActor(),reqObj,timeout,null,request());
			return res;
		} catch (Exception e) {
			return Promise.<Result> pure(createCommonExceptionResponse(e,request()));
		}
	}
     
	/**
	 * This method will update course entry data. course update is allowed
	 * only if course is not published, once course is published we can only update the
	 * status to Retired other filed can't be updated. if course status is not live then 
	 * update on other fields are valid. if user is making course status as Retired then we need to 
	 * updated inside cassandra as well as ES. 
	 * @return Promise<Result>
	 */
	public Promise<Result> updateCourse() {
		try {
			JsonNode requestData = request().body().asJson();
			logger.info("update course request=" + requestData);
			ProjectLogger.log("update course request=" + requestData, LoggerEnum.INFO.name());
			Request reqObj = (Request) mapper.RequestMapper.mapRequest(requestData, Request.class);
			RequestValidator.validateUpdateCourse(reqObj);
			reqObj.setOperation(ActorOperations.UPDATE_COURSE.getValue());
	        reqObj.setRequest_id(ExecutionContext.getRequestId());
	        reqObj.setEnv(getEnvironment());
			HashMap<String, Object> innerMap = new HashMap<>();
			innerMap.put(JsonKey.COURSE, reqObj.getRequest());
			innerMap.put(JsonKey.REQUESTED_BY,getUserIdByAuthToken(request().getHeader(HeaderParam.X_Authenticated_Userid.getName())));
			reqObj.setRequest(innerMap);
			Timeout timeout = new Timeout(Akka_wait_time, TimeUnit.SECONDS);
			Promise<Result> res = actorResponseHandler(getRemoteActor(),reqObj,timeout,null,request());
			return res;
		} catch (Exception e) {
			return Promise.<Result> pure(createCommonExceptionResponse(e,request()));
		}
	}

	
	/**
	 * This method will publish the course. when ever course will be 
	 * published , internally we need to make EkStep api call to collect all
	 * the content related to this course and put into ES. 
	 * @return Promise<Result>
	 */
	public Promise<Result> publishCourse() {
		try {
			JsonNode requestData = request().body().asJson();
			logger.info("published course request =" + requestData);
			ProjectLogger.log("published course request =" + requestData, LoggerEnum.INFO.name());
			Request reqObj = (Request) mapper.RequestMapper.mapRequest(requestData, Request.class);
			RequestValidator.validatePublishCourse(reqObj);
			reqObj.setOperation(ActorOperations.PUBLISH_COURSE.getValue());
	        reqObj.setRequest_id(ExecutionContext.getRequestId());
	        reqObj.setEnv(getEnvironment());
			HashMap<String, Object> innerMap = new HashMap<>();
			innerMap.put(JsonKey.COURSE, reqObj.getRequest());
			innerMap.put(JsonKey.REQUESTED_BY,getUserIdByAuthToken(request().getHeader(HeaderParam.X_Authenticated_Userid.getName())));
			reqObj.setRequest(innerMap);
			Timeout timeout = new Timeout(Akka_wait_time, TimeUnit.SECONDS);
			Promise<Result> res = actorResponseHandler(getRemoteActor(),reqObj,timeout,null,request());
			return res;
		} catch (Exception e) {
			return Promise.<Result> pure(createCommonExceptionResponse(e,request()));
		}
	}

	/**
	 * This method will do the course search. Based on front end 
	 * search query we need to make ElasticSearch search api call to get the data.
	 * In ES we will only search for Live course.
	 * @return Promise<Result>
	 */
	public Promise<Result> searchCourse() {
		try {
			JsonNode requestData = request().body().asJson();
			logger.info("search course request =" + requestData);
			ProjectLogger.log("search course request =" + requestData, LoggerEnum.INFO.name());
			Request reqObj = (Request) mapper.RequestMapper.mapRequest(requestData, Request.class);
			reqObj.setRequest_id(ExecutionContext.getRequestId());
			reqObj.setEnv(getEnvironment());
			reqObj.setOperation(ActorOperations.SEARCH_COURSE.getValue());
			HashMap<String, Object> innerMap = new HashMap<>();
			innerMap.put(JsonKey.SEARCH, reqObj.getRequest());
			innerMap.put(JsonKey.REQUESTED_BY,getUserIdByAuthToken(request().getHeader(HeaderParam.X_Authenticated_Userid.getName())));
			reqObj.setRequest(innerMap);
			Timeout timeout = new Timeout(Akka_wait_time, TimeUnit.SECONDS);
			Promise<Result> res = actorResponseHandler(getRemoteActor(),reqObj,timeout,null,request());
			return res;
		} catch (Exception e) {
			return Promise.<Result> pure(createCommonExceptionResponse(e,request()));
		}
	}
    
	/**
	 * This method will make the course as retired  in Cassandra and ES both
	 * @return Promise<Result>
	 */
	public Promise<Result> deleteCourse() {
		try {
			JsonNode requestData = request().body().asJson();
			logger.info("delete course request =" + requestData);
			ProjectLogger.log("delete course request =" + requestData, LoggerEnum.INFO.name());
			Request reqObj = (Request) mapper.RequestMapper.mapRequest(requestData, Request.class);
			reqObj.setOperation(ActorOperations.DELETE_COURSE.getValue());
	        reqObj.setRequest_id(ExecutionContext.getRequestId());
	        reqObj.setEnv(getEnvironment());
			HashMap<String, Object> innerMap = new HashMap<>();
			innerMap.put(JsonKey.COURSE, reqObj.getRequest());
			innerMap.put(JsonKey.REQUESTED_BY,getUserIdByAuthToken(request().getHeader(HeaderParam.X_Authenticated_Userid.getName())));
			reqObj.setRequest(innerMap);
			Timeout timeout = new Timeout(Akka_wait_time, TimeUnit.SECONDS);
			Promise<Result> res = actorResponseHandler(getRemoteActor(),reqObj,timeout,null,request());
			return res;
		} catch (Exception e) {
			return Promise.<Result> pure(createCommonExceptionResponse(e,request()));
		}
	}

	
	/**
	 * This method will do the course search based on course id.
	 * search will happen under ES.
	 * @param courseId Stirng
	 * @return Promise<Result>
	 */
	public Promise<Result> getCourseById(String courseId) {
		try {
			logger.info("get course request =" + courseId);
			ProjectLogger.log("get course request =" + courseId, LoggerEnum.INFO.name());
			Request reqObj = new Request();
			reqObj.setRequest_id(ExecutionContext.getRequestId());
			reqObj.setEnv(getEnvironment());
			reqObj.setOperation(ActorOperations.GET_COURSE_BY_ID.getValue());
			HashMap<String, Object> innerMap = new HashMap<>();
			innerMap.put(JsonKey.ID, courseId);
			innerMap.put(JsonKey.REQUESTED_BY,getUserIdByAuthToken(request().getHeader(HeaderParam.X_Authenticated_Userid.getName())));
			reqObj.setRequest(innerMap);
			Timeout timeout = new Timeout(Akka_wait_time, TimeUnit.SECONDS);
			Promise<Result> res = actorResponseHandler(getRemoteActor(),reqObj,timeout,null,request());
			return res;
		} catch (Exception e) {
			return Promise.<Result> pure(createCommonExceptionResponse(e,request()));
		}
	}

	/**
	 * This method will provide the recommended courses for particular user.
	 * search will happen under ES.
	 * @return Promise<Result>
	 */
	public Promise<Result> recommendedCourses() {
		try {
			logger.info("Method Started # RECOMMENDED COURSES");
			ProjectLogger.log("Method Started # RECOMMENDED COURSES");
			Request reqObj = new Request();
			reqObj.setRequest_id(ExecutionContext.getRequestId());
			reqObj.setEnv(getEnvironment());
			reqObj.setOperation(ActorOperations.GET_RECOMMENDED_COURSES.getValue());
			HashMap<String, Object> innerMap = new HashMap<String, Object>();
			innerMap.put(JsonKey.REQUESTED_BY,getUserIdByAuthToken(request().getHeader(HeaderParam.X_Authenticated_Userid.getName())));
			reqObj.setRequest(innerMap);
			Timeout timeout = new Timeout(Akka_wait_time, TimeUnit.SECONDS);
			Promise<Result> res = actorResponseHandler(getRemoteActor(),reqObj,timeout,null,request());
			return res;
		} catch (Exception e) {
			return Promise.<Result> pure(createCommonExceptionResponse(e,request()));
		}
	}
}
