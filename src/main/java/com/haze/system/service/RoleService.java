package com.haze.system.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.haze.core.service.AbstractBaseService;
import com.haze.system.dao.RoleDao;
import com.haze.system.entity.Resource;
import com.haze.system.entity.Role;
import com.haze.system.exception.RoleExistException;
import com.haze.system.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


/**
 * 角色对象Service
 * @author sofar
 *
 */
@Service
public class RoleService extends AbstractBaseService<Role, Long> {
	
	private RoleDao roleDao;
	
	@Autowired
	public void setRoleDao(RoleDao roleDao) {
		this.roleDao = roleDao;
		super.setDao(roleDao);
	}
	
	public List<Role> findByStatus(Status status) {
		return this.roleDao.findByStatus(status);
	}
	
	/**
	 * 根据角色状态获取系统中所有的角色名称
	 * @param status 角色状态
	 * @return 角色名称集合
	 */
	public List<String> findAllRoleNameByStatus(Status status) {
		List<String> roleNameList = new ArrayList<String>();
		List<Role> roleList = findByStatus(status);
		for (Role role : roleList) {
			roleNameList.add(role.getName());
		}
		return roleNameList;
	}
	/**
	 * 保存角色对象，保存前会校验角色名称是否存在，如果角色名称已存在则抛出RoleExistException
	 * @param role 角色对象
	 * @return 
	 * @throws RoleExistException 角色已存在对象
	 */
	@CacheEvict(value="shiroCache",allEntries=true)
	@Transactional(readOnly = false)
	public Role saveOrUpdate(Role role) throws RoleExistException {
		Assert.notNull(role.getName());
		Role r = this.roleDao.findByCode(role.getCode());
		if (null != r && role.getId() == null) {
			logger.error("角色保存失败，角色名称{}已存在！" + role.getName());
			throw new RoleExistException("角色名称" + role.getName() + "已存在");
		} else {
			logger.info("保存角色成功，角色名为{}", role.getName());
			this.roleDao.save(role);
		}
		return role;
	}
	
	/**
	 * 删除所有角色，同时删除和角色关联的用户，组织，资源的关联信息
	 * @throws Exception
	 */

	@CacheEvict(value="shiroCache",allEntries=true)
	@Transactional(readOnly = false)
	public void deleteALl() throws Exception {
		List<Role> roleList = this.findAll();
		for (Role role : roleList) {
			delete(role.getId());
		}
	}
	
	/**
	 * 删除角色 同时删除和该角色关联的用户 ， 资源的关联信息
	 * @param id 角色Id
	 * @throws Exception
	 */
	@CacheEvict(value="shiroCache",allEntries=true)
	@Transactional(readOnly = false)
	public void delete(Long id) throws Exception {
		Role role = this.roleDao.getOne(id);
		if (role != null) {
			role.setUsers(null);
			/*Set<User> users = role.getUsers();
			for (User user : users) {
				user.removeRole(role);
			}*/
			role.setResources(null);
		}
		this.deleteById(id);
	}

	/**
	 * 批量删除角色 同时删掉用户 资源和角色的关联关系
	 * @param ids 角色Id集合
	 * @throws Exception
	 */
	@CacheEvict(value="shiroCache",allEntries=true)
	@Transactional(readOnly = false)
	public void batchDelete(Long[] ids) throws Exception {
		for (Long id : ids) {
			delete(id);
		}
	}

	@CacheEvict(value="shiroCache",allEntries=true)
	@Transactional(readOnly = false)
	public void addResources(Long id, Long[] resourceIds) throws Exception {
        Assert.notNull(id);
		Role role = this.findById(id);
		Set<Resource> resources = new HashSet<Resource>();
		for (Long resourceId : resourceIds) {
			Resource resource = new Resource();
			resource.setId(resourceId);
			resources.add(resource);
			role.setResources(resources);
		}
		this.saveOrUpdate(role);
	}
	
	/**
	 * 更新角色对象，同时清除shiro缓存
	 * @param role 角色对象
	 */
	@CacheEvict(value="shiroCache",allEntries=true)
	@Transactional(readOnly = false)
	public Role updateRole(Role role) {
		return this.roleDao.save(role);
	}

    /**
     * 判断角色代码为{@code code}的角色对象是否存在
     * @param code 角色代码
     * @return 存在返回true,否则返回false
     */
	public Boolean existCode(String code) {
		Role role = this.roleDao.findByCode(code);
		return role != null;
	}
	
}


