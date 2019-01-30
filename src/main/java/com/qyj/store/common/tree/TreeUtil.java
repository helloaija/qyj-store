package com.qyj.store.common.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qyj.store.entity.SysMenuModel;

public class TreeUtil {

	public static TreeNode loadTreeNode(TreeNode parentNode, List<SysMenuModel> sysMenuList) throws Exception {

		if (sysMenuList == null || sysMenuList.isEmpty()) {
			return null;
		}

		List<TreeNode> children = new ArrayList<TreeNode>();

		SysMenuModel menuModel = null;
		for (int i = 0; i < sysMenuList.size(); i++) {
			menuModel = sysMenuList.get(i);

			if (menuModel.getParentId() != null
					&& (menuModel.getParentId().toString().equals(parentNode.getId().toString()))) {
				children.add(loadTreeNode(convertModelToNode(menuModel), sysMenuList));
			}
		}

		if (!children.isEmpty()) {
			parentNode.setChildren(children);
		}

		return parentNode;
	}

	public static TreeNode convertModelToNode(SysMenuModel menuModel) {
		Long id = menuModel.getId();
		String text = menuModel.getName();

		TreeNode node = new TreeNode(id, text);
		node.setChecked(menuModel.getChecked());
		
		Map<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("type", menuModel.getMenuType());
		node.setAttributes(attributesMap);

		return node;
	}
}
