/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.radixware.jiraclient.implementation.soap;

import com.atlassian.jira.rpc.soap.client.JiraSoapService;
import com.atlassian.jira.rpc.soap.client.JiraSoapServiceServiceLocator;
import com.atlassian.jira.rpc.soap.client.RemoteAttachment;
import com.atlassian.jira.rpc.soap.client.RemoteComment;
import com.atlassian.jira.rpc.soap.client.RemoteCustomFieldValue;
import com.atlassian.jira.rpc.soap.client.RemoteFilter;
import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import com.atlassian.jira.rpc.soap.client.RemoteIssueType;
import com.atlassian.jira.rpc.soap.client.RemoteNamedObject;
import com.atlassian.jira.rpc.soap.client.RemotePriority;
import com.atlassian.jira.rpc.soap.client.RemoteProject;
import com.atlassian.jira.rpc.soap.client.RemoteResolution;
import com.atlassian.jira.rpc.soap.client.RemoteStatus;
import com.atlassian.jira.rpc.soap.client.RemoteUser;
import com.atlassian.jira.rpc.soap.client.RemoteValidationException;
import com.atlassian.jira.rpc.soap.client.RemoteVersion;
import com.atlassian.jira.rpc.soap.client.RemoteWorklog;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.xml.rpc.ServiceException;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.EngineConfigurationFactoryDefault;
import org.radixware.jiraclient.implementation.common.AbstractJiraClient;
import org.radixware.jiraclient.implementation.common.CustomFieldImpl;
import org.radixware.jiraclient.exception.JiraClientException;
import org.radixware.jiraclient.exception.JiraObjectNotFoundException;
import org.radixware.jiraclient.implementation.soap.ssl.SoapJiraSocketFactory;
import org.radixware.jiraclient.wrap.Action;
import org.radixware.jiraclient.wrap.Attachment;
import org.radixware.jiraclient.wrap.Comment;
import org.radixware.jiraclient.wrap.CustomField;
import org.radixware.jiraclient.wrap.Issue;
import org.radixware.jiraclient.wrap.IssueType;
import org.radixware.jiraclient.wrap.ParentIssue;
import org.radixware.jiraclient.wrap.Priority;
import org.radixware.jiraclient.wrap.Project;
import org.radixware.jiraclient.wrap.Resolution;
import org.radixware.jiraclient.wrap.SearchFilter;
import org.radixware.jiraclient.wrap.Status;
import org.radixware.jiraclient.wrap.Subtask;
import org.radixware.jiraclient.wrap.User;
import org.radixware.jiraclient.wrap.Version;
import org.radixware.jiraclient.wrap.Worklog;
import org.radixware.jiraclient.wrap.input.AttachmentInput;
import org.radixware.jiraclient.wrap.input.CommentInput;
import org.radixware.jiraclient.wrap.input.ParentIssueInput;
import org.radixware.jiraclient.wrap.input.SubtaskInput;
import org.radixware.jiraclient.wrap.input.WorklogInput;

/**
 * Soap JIRA Remote Client.
 *
 * @author ashamsutdinov
 */
public final class SoapJiraClient extends AbstractJiraClient {

	private URL jiraServerURL;
	private JiraSoapService service;
	private String token = "";
	private final String appendPath = "/rpc/soap/jirasoapservice-v2";

	public SoapJiraClient(final URL jiraServerURL, final String username, final String password) throws JiraClientException {
		super(new ExternalAttachmentHandler(username, password));
		basicAuth(jiraServerURL, username, password);
	}

	@SuppressWarnings("unchecked")
	public SoapJiraClient(final URL jiraServerURL, final SSLContext sslContext) throws JiraClientException {
		super(new ExternalAttachmentHandler(sslContext));
		// it is necessary because there is no method to set user socket factory without using AxisProperties
		// and no opportunity to bind user factory with https protocol
		AccessController.doPrivileged(new PrivilegedAction() {
			@Override
			public Object run() {
				try {
					Class<?> clazz = Class.forName(org.apache.axis.components.net.SocketFactoryFactory.class.getCanonicalName());
					Field field = clazz.getDeclaredField("factories");
					field.setAccessible(true);
					Hashtable table = (Hashtable) field.get(null);
					table.put("https", new SoapJiraSocketFactory(sslContext));
					field.set(null, table);
				} catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ex) {
					throw new JiraClientException(ex);
				}
				return null;
			}
		});
		basicAuth(jiraServerURL, "", "");
	}

	protected final void basicAuth(final URL jiraServerURL, final String username, final String password) {
		this.jiraServerURL = jiraServerURL;
		try {
			EngineConfiguration config = EngineConfigurationFactoryDefault.newFactory(null).getClientEngineConfig();
			URL fullJiraSoapPath = new URL(jiraServerURL.toString() + appendPath);
			service = new JiraSoapServiceServiceLocator(config).getJirasoapserviceV2(fullJiraSoapPath);
			token = service.login(username, password);
		} catch (RemoteException | ServiceException | MalformedURLException ex) {
			throw new JiraClientException(ex);
		}
	}

	byte[] getAttachmentBytes(final URL attachmentURL) throws JiraClientException {
		return externalAttachmentHandler.getBytes(attachmentURL);
	}

	InputStream getAttachmentInputStream(final URL attachmentURL) throws JiraClientException {
		return externalAttachmentHandler.getInputStream(attachmentURL);
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			super.finalize();
			service.logout(token);
			token = "";
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public User getUser(final String name) {
		try {
			RemoteUser user = service.getUser(token, name);
			if(user == null) {
				throw new JiraObjectNotFoundException("User not found");
			} else {
				return new SoapUser(user);
			}
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Iterable<IssueType> getIssueTypes() {
		try {
			RemoteIssueType[] inList = service.getIssueTypes(token);
			List<IssueType> outList = new ArrayList<>();
			for (RemoteIssueType rit : inList) {
				outList.add(new SoapIssueType(rit));
			}
			return Collections.unmodifiableList(outList);
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}

	}

	@Override
	public IssueType getIssueType(final String id) {
		try {
			RemoteIssueType[] inList = service.getIssueTypes(token);
			for (RemoteIssueType rit : inList) {
				if(rit.getId().equals(id)) {
					return new SoapIssueType(rit);
				}
			}
			throw new JiraObjectNotFoundException("Issue type with id '" + id + "' doesn't exist or you don't have permission");
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public IssueType getIssueTypeByName(final String issueTypeName) {
		try {
			RemoteIssueType[] inList = service.getIssueTypes(token);
			for (RemoteIssueType rit : inList) {
				if(rit.getName().equals(issueTypeName)) {
					return new SoapIssueType(rit);
				}
			}
			throw new JiraObjectNotFoundException("Issue type with name '" + issueTypeName + "' doesn't exist or you don't have permission");
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public IssueType getSubtaskType(final String id) {
		try {
			RemoteIssueType[] inList = service.getSubTaskIssueTypes(token);
			for (RemoteIssueType rit : inList) {
				if(rit.getId().equals(id)) {
					return new SoapIssueType(rit);
				}
			}
			throw new JiraObjectNotFoundException("Subtask type with id '" + id + "' not found");
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public IssueType getSubtaskTypeByName(final String subtaskTypeName) {
		try {
			RemoteIssueType[] inList = service.getSubTaskIssueTypes(token);
			for (RemoteIssueType rit : inList) {
				if(rit.getName().equals(subtaskTypeName)) {
					return new SoapIssueType(rit);
				}
			}
			throw new JiraObjectNotFoundException("Subtask type with name '" + subtaskTypeName + "' not found");
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Iterable<Priority> getPriorities() {
		try {
			RemotePriority[] inList = service.getPriorities(token);
			List<Priority> outList = new ArrayList<>();
			for (RemotePriority rp : inList) {
				outList.add(new SoapPriority(rp));
			}
			return Collections.unmodifiableList(outList);
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Priority getPriority(final String id) {
		try {
			RemotePriority[] inList = service.getPriorities(token);
			for (RemotePriority rp : inList) {
				if(rp.getId().equals(id)) {
					return new SoapPriority(rp);
				}
			}
			throw new JiraObjectNotFoundException("Issue priority with id '" + id + "' doesn't exist or you don't have permission");
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Priority getPriorityByName(final String priorityName) {
		try {
			RemotePriority[] inList = service.getPriorities(token);
			for (RemotePriority rp : inList) {
				if(rp.getName().equals(priorityName)) {
					return new SoapPriority(rp);
				}
			}
			throw new JiraObjectNotFoundException("Issue priority with name '" + priorityName + "' doesn't exist or you don't have permission");
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Iterable<Resolution> getResolutions() {
		List<Resolution> outList = new ArrayList<>();
		RemoteResolution[] inList;
		try {
			inList = service.getResolutions(token);
			for (RemoteResolution rr : inList) {
				outList.add(new SoapResolution(rr));
			}
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
		return outList;
	}

	@Override
	public Resolution getResolution(final String id) {
		try {
			RemoteResolution[] inList = service.getResolutions(token);
			for (RemoteResolution rr : inList) {
				if(rr.getId().equals(id)) {
					return new SoapResolution(rr);
				}
			}
			throw new JiraObjectNotFoundException("Issue resolution with id '" + id + "' doesn't exist or you don't have permission");
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Resolution getResolutionByName(final String resolutionName) {
		try {
			RemoteResolution[] inList = service.getResolutions(token);
			for (RemoteResolution rr : inList) {
				if(rr.getName().equals(resolutionName)) {
					return new SoapResolution(rr);
				}
			}
			throw new JiraObjectNotFoundException("Issue resolution with name '" + resolutionName + "' doesn't exist or you don't have permission");
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Iterable<Status> getStatuses() {
		try {
			RemoteStatus[] inList = service.getStatuses(token);
			List<Status> outList = new ArrayList<>();
			for (RemoteStatus rs : inList) {
				outList.add(new SoapStatus(rs));
			}
			return Collections.unmodifiableList(outList);
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Status getStatus(final String id) {
		try {
			RemoteStatus[] inList = service.getStatuses(token);
			for (RemoteStatus rs : inList) {
				if(rs.getId().equals(id)) {
					return new SoapStatus(rs);
				}
			}
			throw new JiraObjectNotFoundException("Issue status with id '" + id + "' doesn't exist or you don't have permission");
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}

	}

	@Override
	public Status getStatusByName(final String statusName) {
		try {
			RemoteStatus[] inList = service.getStatuses(token);
			for (RemoteStatus rs : inList) {
				if(rs.getName().equals(statusName)) {
					return new SoapStatus(rs);
				}
			}
			throw new JiraObjectNotFoundException("Issue status with name '" + statusName + "' doesn't exist or you don't have permission");
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Iterable<Project> getProjects() {
		try {
			RemoteProject[] inList = service.getProjectsNoSchemes(token);
			List<Project> outList = new ArrayList<>();
			for (RemoteProject rp : inList) {
				outList.add(new SoapProject(rp, this));
			}
			return Collections.unmodifiableList(outList);
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Project getProject(final String projectKey) {
		try {
			RemoteProject project = service.getProjectByKey(token, projectKey);
			return new SoapProject(project, this);
		} catch (RemoteValidationException ex) {
			throw new JiraObjectNotFoundException("Project with key '" + projectKey + "' doesn't exist");
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Iterable<Issue> getIssuesByFilterQuery(final String query, final int answerSize) {
		try {
			int newAnswerSize = answerSize < 1 || answerSize > MAX_SEARCH_RESULTS_SIZE
					? DEFAULT_SEARCH_RESULTS_SIZE
					: answerSize;

			RemoteIssue[] inList = service.getIssuesFromJqlSearch(token, query, newAnswerSize);
			List<Issue> outList = new ArrayList<>();
			for (RemoteIssue ri : inList) {
				outList.add(new SoapParentIssue(ri, this));
			}
			return Collections.unmodifiableList(outList);

		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	/**
	 * Service method. Necessary for return all subtasks of a specified parent issue.
	 */
	Iterable<Subtask> getOnlySubtasks(final String parentKey) {
		try {
			int newAnswerSize = MAX_SEARCH_RESULTS_SIZE;
			ParentIssue parent = getParentIssue(parentKey);
			RemoteIssue[] inList = service.getIssuesFromJqlSearch(token, "parent = " + parentKey, newAnswerSize);
			List<Subtask> outList = new ArrayList<>();
			for (RemoteIssue ri : inList) {
				outList.add(new SoapSubtask(ri, parent, this));
			}
			return Collections.unmodifiableList(outList);

		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Issue getIssue(final String issueKey) {
		try {
			return new SoapParentIssue(service.getIssue(token, issueKey), this);
		} catch (RemoteException ex) {
			throw new JiraObjectNotFoundException("Issue with key '" + issueKey + "' doesn't exist or you don't have permission");
		}
	}

	@Override
	public ParentIssue createParentIssue(final ParentIssueInput newIssue) {

		int size = 0;
		int index = 0;

		RemoteIssue issue = new RemoteIssue();

		Calendar date = Calendar.getInstance();
		RemoteVersion[] affectsVersions;
		Iterable<Version> versions = newIssue.getAffectsVersions();
		if(versions != null) {
			while (versions.iterator().hasNext()) {
				size++;
			}
			affectsVersions = new RemoteVersion[size];

			for (Version vi : versions) {
				date.setTime(vi.getReleaseDate());
				affectsVersions[index] = new RemoteVersion(vi.getId(), vi.getName(), vi.isArchived(), date, vi.isReleased(), Long.MIN_VALUE);
				index++;
			}
			issue.setAffectsVersions(affectsVersions);
		}

		size = 0;
		index = 0;

		RemoteVersion[] fixVersions;
		versions = newIssue.getFixVersions();
		if(versions != null) {
			while (versions.iterator().hasNext()) {
				size++;
			}
			fixVersions = new RemoteVersion[size];

			for (Version vi : versions) {
				date.setTime(vi.getReleaseDate());
				fixVersions[index] = new RemoteVersion(vi.getId(), vi.getName(), vi.isArchived(), date, vi.isReleased(), Long.MIN_VALUE);
				index++;
			}
			issue.setFixVersions(fixVersions);
		}

		issue.setAssignee(newIssue.getAssignee().getName());
		issue.setReporter(newIssue.getReporter().getName());
		issue.setPriority(newIssue.getPriority().getId());
		issue.setType(newIssue.getIssueType().getId());
		issue.setProject(newIssue.getProject().getKey());

		issue.setSummary(newIssue.getSummary());

		issue.setDescription(newIssue.getDescription());

		try {
			return new SoapParentIssue(service.createIssue(token, issue), this);
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public void deleteIssue(final Issue issue) {
		try {
			service.deleteIssue(token, issue.getKey());
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Comment addComment(final Issue issue, final CommentInput comment) {
		try {
			RemoteComment rcomment = new RemoteComment();

			rcomment.setBody(comment.getBody());

			service.addComment(token, issue.getKey(), rcomment);

			RemoteComment[] comments = service.getComments(token, issue.getKey());

			// oops, created comment has no id
			rcomment.setId(comments[comments.length - 1].getId());

			return new SoapComment(rcomment);

		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Issue doWorkflowAction(final Issue issue, final Action action) {
		try {
			RemoteIssue ri = service.progressWorkflowAction(token, issue.getKey(), action.getId(), null);
			return new SoapParentIssue(ri, this);
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Iterable<IssueType> getSubtaskTypes() {
		try {
			RemoteIssueType[] inList = service.getSubTaskIssueTypes(token);
			List<IssueType> outList = new ArrayList<>();
			for (RemoteIssueType rit : inList) {
				outList.add(new SoapIssueType(rit));
			}
			return Collections.unmodifiableList(outList);
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Iterable<Action> getAvailableWorkflowActions(final Issue issue) {
		try {
			RemoteNamedObject[] inList = service.getAvailableActions(token, issue.getKey());
			List<Action> outList = new ArrayList<>();
			for (RemoteNamedObject rno : inList) {
				outList.add(new SoapAction(rno));
			}
			return Collections.unmodifiableList(outList);
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Subtask createSubtask(final SubtaskInput newSubtask, final ParentIssue parentIssue) {

		int size = 0;
		int index = 0;

		RemoteIssue issue = new RemoteIssue();
		Calendar date = Calendar.getInstance();
		RemoteVersion[] affectsVersions;
		Iterable<Version> versions = newSubtask.getAffectsVersions();
		if(versions != null) {
			for (Version vi : versions) {
				size++;
			}
			affectsVersions = new RemoteVersion[size];


			for (Version vi : versions) {
				date.setTime(vi.getReleaseDate());
				affectsVersions[index] = new RemoteVersion(vi.getId(), vi.getName(), vi.isArchived(), date, vi.isReleased(), Long.MIN_VALUE);
				index++;
			}
			issue.setAffectsVersions(affectsVersions);
		}

		size = 0;
		index = 0;

		RemoteVersion[] fixVersions;
		versions = newSubtask.getFixVersions();
		if(versions != null) {
			while (versions.iterator().hasNext()) {
				size++;
			}
			fixVersions = new RemoteVersion[size];

			for (Version vi : versions) {
				date.setTime(vi.getReleaseDate());
				fixVersions[index] = new RemoteVersion(vi.getId(), vi.getName(), vi.isArchived(), date, vi.isReleased(), Long.MIN_VALUE);
				index++;
			}
			issue.setFixVersions(fixVersions);
		}

		issue.setAssignee(newSubtask.getAssignee().getName());
		issue.setReporter(newSubtask.getReporter().getName());
		issue.setPriority(newSubtask.getPriority().getId());
		issue.setType(newSubtask.getIssueType().getId());
		issue.setProject(newSubtask.getProject().getKey());

		issue.setSummary(newSubtask.getSummary());

		issue.setDescription(newSubtask.getDescription());
		try {
			SoapSubtask answerSubtask = new SoapSubtask(service.createIssueWithParent(token, issue, parentIssue.getKey()), parentIssue, this);
			return answerSubtask;
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Worklog addWorklog(final Issue issue, final WorklogInput worklog) {
		try {
			RemoteWorklog remoteWorklog = new RemoteWorklog();
			remoteWorklog.setComment(worklog.getComment());

			Calendar date = Calendar.getInstance();
			date.setTime(worklog.getStartDate());
			remoteWorklog.setStartDate(date);

			remoteWorklog.setTimeSpentInSeconds(worklog.getTimeSpentInMinutes() * 60L);
			remoteWorklog.setTimeSpent(Integer.valueOf(worklog.getTimeSpentInMinutes()).toString());

			RemoteWorklog answerRemoteWorklog = service.addWorklogAndAutoAdjustRemainingEstimate(token, issue.getKey(), remoteWorklog);

			return new SoapWorklog(answerRemoteWorklog, this);
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Comment editComment(@SuppressWarnings("unused") final Issue issue, final Comment comment, final String newBody) {
		try {
			RemoteComment rc = service.getComment(token, Long.parseLong(comment.getId()));
			rc.setBody(newBody);
			return new SoapComment(service.editComment(token, rc));
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Iterable<Comment> getComments(final Issue issue) {
		try {
			RemoteComment[] inList = service.getComments(token, issue.getKey());
			List<Comment> outList = new ArrayList<>();
			for (RemoteComment rc : inList) {
				outList.add(new SoapComment(rc));
			}
			return Collections.unmodifiableList(outList);
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Iterable<Worklog> getWorklogs(final Issue issue) {
		try {
			RemoteWorklog[] inList = service.getWorklogs(token, issue.getKey());
			List<Worklog> outList = new ArrayList<>();
			for (RemoteWorklog rw : inList) {
				outList.add(new SoapWorklog(rw, this));
			}
			return Collections.unmodifiableList(outList);
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Iterable<CustomField> getCustomFields(final Issue issue) {
		try {

			RemoteCustomFieldValue[] inList = service.getIssue(token, issue.getKey()).getCustomFieldValues();
			List<CustomField> outList = new ArrayList<>();

			for (RemoteCustomFieldValue rcfv : inList) {
				outList.add(new CustomFieldImpl(rcfv.getCustomfieldId(), null, null, rcfv.getValues()));
			}
			return Collections.unmodifiableList(outList);
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public String getJiraVersion() {
		try {
			return service.getServerInfo(token).getVersion();
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Iterable<Version> getProjectVersions(final Project project) {
		try {
			RemoteVersion[] inList = service.getVersions(token, project.getKey());
			List<Version> outList = new ArrayList<>();
			for (RemoteVersion rv : inList) {
				outList.add(new SoapVersion(rv));
			}
			return Collections.unmodifiableList(outList);
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public ParentIssue getParentIssue(final String issueKey) {
		Issue answer = getIssue(issueKey);
		if(!answer.getIssueType().isSubtask()) {
			return (ParentIssue) answer;
		} else {
			throw new JiraClientException("Found issue is not a parent");
		}
	}

	@Override
	public Iterable<SearchFilter> getFilters() {
		try {
			RemoteFilter[] inList = service.getFavouriteFilters(token);
			List<SearchFilter> outList = new ArrayList<>();
			for (RemoteFilter rf : inList) {
				outList.add(new SoapSearchFilter(rf));
			}
			return Collections.unmodifiableList(outList);
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Iterable<Issue> getIssuesByFilter(final SearchFilter filter, final int maxResults) {
		try {
			RemoteIssue[] inList = service.getIssuesFromFilterWithLimit(token, filter.getId(), 0, maxResults);
			List<Issue> outList = new ArrayList<>();
			for (RemoteIssue ri : inList) {
				outList.add(new SoapParentIssue(ri, this));
			}
			return Collections.unmodifiableList(outList);
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public boolean addAttachmentToIssue(final Issue issue, final AttachmentInput attachment) {
		try {
			String[] filename = new String[]{attachment.getFilename()};

			byte[][] data = new byte[1][attachment.getFilesize()];

			for (int index = 0; index < attachment.getFilesize(); index++) {
				data[0][index] = (byte) attachment.getFileInputStream().read();
			}
			return service.addAttachmentsToIssue(token, issue.getKey(), filename, data);
		} catch (Exception ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public Iterable<Attachment> getAttachmentsFromIssue(final Issue issue) {
		try {
			RemoteAttachment[] inList = service.getAttachmentsFromIssue(token, issue.getKey());
			List<Attachment> outList = new ArrayList<>();
			for (RemoteAttachment ra : inList) {
				outList.add(new SoapAttachment(ra, this));
			}
			return Collections.unmodifiableList(outList);
		} catch (RemoteException ex) {
			throw new JiraClientException(ex);
		}
	}

	@Override
	public URL getJiraServerURL() {
		return jiraServerURL;
	}
}
