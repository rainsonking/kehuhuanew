package com.kwsoft.kehuhua.bean;

public class PDFOutlineElement {
	private String id;//保存的和初始化的决定性Id
	private String outlineTitle ;//每一项不管父类子类的名字
	private boolean mhasParent; //是否包含父亲
	private boolean mhasChild ;//是否有儿子
	private String parent;//父类的Id
	private int level;//所属层级0为第一层，以此类推
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOutlineTitle() {
		return outlineTitle;
	}

	public void setOutlineTitle(String outlineTitle) {
		this.outlineTitle = outlineTitle;
	}

	public boolean isMhasParent() {
		return mhasParent;
	}

	public void setMhasParent(boolean mhasParent) {
		this.mhasParent = mhasParent;
	}

	public boolean isMhasChild() {
		return mhasChild;
	}

	public void setMhasChild(boolean mhasChild) {
		this.mhasChild = mhasChild;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isExpanded() {
		return expanded;
	}
	public boolean isChecked(){
		return checked;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	//private OutlineElement outlineElement;
	private boolean expanded;
	private boolean checked;
	
	public PDFOutlineElement(String id, String outlineTitle,
							 boolean mhasParent, boolean mhasChild, String parent, int level,
							 boolean expanded, boolean checked) {
		super();
		this.id = id;
		this.outlineTitle = outlineTitle;
		this.mhasParent = mhasParent;
		this.mhasChild = mhasChild;
		this.parent = parent;
		this.level = level;
		this.expanded = expanded;
		this.checked=checked;
	}

	@Override
	public String toString() {
		return "PDFOutlineElement{" +
				"id='" + id + '\'' +
				", outlineTitle='" + outlineTitle + '\'' +
				", mhasParent=" + mhasParent +
				", mhasChild=" + mhasChild +
				", parent='" + parent + '\'' +
				", level=" + level +
				", expanded=" + expanded +
				", checked=" + checked +
				'}';
	}
}

