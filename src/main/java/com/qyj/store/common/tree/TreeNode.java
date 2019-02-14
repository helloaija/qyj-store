package com.qyj.store.common.tree;

import java.util.List;
import java.util.Map;

public class TreeNode {

	public enum TreeNodeState {
		open, closed
	}

	// 节点id
	private Long key;
	// 要显示的文本
	private String title;
	// 节点状态'closed'或'open'
	private TreeNodeState state;
	// 节点是否被选中
	private Boolean checked;
	// 绑定到节点的自定义属性
	private Map<String, String> attributes;
	// 子节点
	private List<TreeNode> children;

	public TreeNode() {
	}

	public TreeNode(Long key, String title) {
		this.key = key;
		this.title = title;
	}

	/**
	 * @return the key
	 */
	public Long getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(Long key) {
		this.key = key;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the state
	 */
	public TreeNodeState getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(TreeNodeState state) {
		this.state = state;
	}

	/**
	 * @return the checked
	 */
	public Boolean getChecked() {
		return checked;
	}

	/**
	 * @param checked the checked to set
	 */
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	/**
	 * @return the attributes
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return the children
	 */
	public List<TreeNode> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}

}
