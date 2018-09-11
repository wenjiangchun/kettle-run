package com.bkht.shiro;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.bkht.core.EncodeUtils;
import com.bkht.core.HazeStringUtils;
import com.bkht.system.entity.Role;
import com.bkht.system.entity.User;
import com.bkht.system.service.ResourceService;
import com.bkht.system.service.RoleService;
import com.bkht.system.service.UserService;
import com.bkht.system.utils.Status;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

public class ShiroRealm extends AuthorizingRealm {

	private static final String PERMS = "perms\\[(.*?)\\]";

	private UserService userService;

	private RoleService roleService;

	private ResourceService resourceService;


	public ShiroRealm(UserService userService, RoleService roleService, ResourceService resourceService) {
	    this.userService = userService;
	    this.roleService = roleService;
	    this.resourceService = resourceService;
    }

	/**
	 * 认证回调函数,登录时调用.
	 */

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken authcToken) {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		User user = userService.findByLoginName(token.getUsername());
		if (user == null) {
                throw new UnknownAccountException("用户名不存在!");
        }
		Status status = user.getStatus();
		if (status == Status.DISABLE) { //账户禁用
			throw new DisabledAccountException();
		}
         else if (status == Status.LOCK) { //账户冻结
			throw new LockedAccountException();
		}
		byte[] salt = EncodeUtils.decodeHex(user.getSalt());
		return new SimpleAuthenticationInfo(new ShiroUser(user.getId(), user.getLoginName(),user.getName()),user.getPassword(), ByteSource.Util.bytes(salt),
				getName());
	}

	/**
	 * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		ShiroUser shiroUser = (ShiroUser) principals.getPrimaryPrincipal();
		User user = userService.findByLoginName(shiroUser.getLoginName());
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		Set<String> permissions = new HashSet<String>();

		if (user.isSuperAdmin()) { // 超级用户
			info.addRoles(roleService.findAllRoleNameByStatus(Status.ENABLE));
			List<String> perms = resourceService.findAllPermission();
			permissions.addAll(HazeStringUtils.getValue(perms, PERMS));

		} else {
			Set<String> perms = new HashSet<String>();
			for (Role role : user.getRoles()) {
				if (role.getStatus() == Status.ENABLE) {
					info.addRole(role.getCode());
					perms.addAll(role.getAllPermissons());
				}
			}
			permissions.addAll(HazeStringUtils.getValue(perms, PERMS)); //添加用户所有资源权限
			//permissions.addAll(perms);
		}
		info.addStringPermissions(permissions);
		return info;
	}

	/**
	 * 设定Password校验的Hash算法与迭代次数.
	 */
    @PostConstruct
	public void initCredentialsMatcher() {
		HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(
				UserService.HASH_ALGORITHM);
		matcher.setHashIterations(UserService.HASH_INTERATIONS);
		setCredentialsMatcher(matcher);
	}

    private List<String> defaultPermissions;

	public List<String> getDefaultPermissions() {
		return defaultPermissions;
	}

	public void setDefaultPermissions(List<String> defaultPermissions) {
		this.defaultPermissions = defaultPermissions;
	}
    
}
