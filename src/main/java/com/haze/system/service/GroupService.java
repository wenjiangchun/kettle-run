package com.haze.system.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.haze.core.service.AbstractBaseService;
import com.haze.core.service.HazeServiceException;
import com.haze.system.dao.GroupDao;
import com.haze.system.dao.UserDao;
import com.haze.system.entity.Group;
import com.haze.system.entity.User;
import com.haze.system.utils.Status;
import com.haze.system.utils.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 组织机构业务操作类
 * @author Sofar
 *
 */
@Service
@Transactional
public class GroupService extends AbstractBaseService<Group, Long> {

    private GroupDao groupDao;

    @Autowired
	public void setGroupDao(GroupDao groupDao) {
        this.groupDao = groupDao;
		super.setDao(groupDao);
	}
	
	@Transactional(readOnly=true)
	public List<Group> getTopGroups() {
		return groupDao.getTopGroups();
	}

	@Override
	public void delete(Group t) throws HazeServiceException {
        this.groupDao.delete(t);
	}

	@Override
	public void deleteById(Long id) throws HazeServiceException {
        this.groupDao.deleteById(id);
	}

	/**
	 * 获取系统中状态为启用的机构和该机构下状态为启用的用户树节点信息
	 * @return List 机构和树节点信息列表
	 */
	public List<TreeNode> getTreeNode() {
		List<Group> groups = this.findByProperty("status", Status.ENABLE);
		List<TreeNode> treeNodes = new ArrayList<>();
		for (Group g : groups) {
			TreeNode treeNode = new TreeNode();
			treeNode.setId(g.getId());
			treeNode.setName(g.getFullName());
			treeNode.setParentId(g.getPid());
			treeNode.setOpen(false);
			treeNode.setNodeType("group");
			Set<User> users = g.getUsers();
			users.addAll(userDao.findByProperty("bakGroupId", g.getId()));
			for (User u : users) {
				if (u.getStatus() == Status.ENABLE) {
					TreeNode treeNode1 = new TreeNode();
					treeNode1.setId(u.getId());
					treeNode1.setName(u.getName());
					treeNode1.setParentId(g.getId());
					treeNodes.add(treeNode1);
					treeNode1.setNodeType("user");
				}
			}
			treeNodes.add(treeNode);
		}
		return treeNodes;
	}

	public List<TreeNode> getTreeNode(Long groupId) {
		Group g = this.findById(groupId);
		List<TreeNode> treeNodes = new ArrayList<>();
			TreeNode treeNode = new TreeNode();
			treeNode.setId(g.getId());
			treeNode.setName(g.getFullName());
			treeNode.setParentId(g.getPid());
			treeNode.setOpen(true);
			treeNode.setNodeType("group");
		Set<User> users = g.getUsers();
		users.addAll(userDao.findByProperty("bakGroupId", groupId));
			for (User u : users) {
				if (u.getStatus() == Status.ENABLE) {
					TreeNode treeNode1 = new TreeNode();
					treeNode1.setId(u.getId());
					treeNode1.setName(u.getName());
					treeNode1.setParentId(g.getId());
					treeNodes.add(treeNode1);
					treeNode1.setNodeType("user");
				}
			}
			treeNodes.add(treeNode);
		return treeNodes;
	}

	@Autowired
	private UserDao userDao;
}
