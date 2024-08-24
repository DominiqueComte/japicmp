package japicmp.model;

import japicmp.util.OptionalHelper;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Optional;

public class JApiClassType implements JApiHasChangeStatus {
	private final Optional<ClassType> oldTypeOptional;
	private final Optional<ClassType> newTypeOptional;
	private final JApiChangeStatus changeStatus;

	public enum ClassType {
		ANNOTATION, INTERFACE, CLASS, ENUM
	}

	public JApiClassType(Optional<ClassType> oldTypeOptional, Optional<ClassType> newTypeOptional, JApiChangeStatus changeStatus) {
		this.oldTypeOptional = oldTypeOptional;
		this.newTypeOptional = newTypeOptional;
		this.changeStatus = changeStatus;
	}

	@XmlAttribute
	public String getOldType() {
		return OptionalHelper.optionalToString(oldTypeOptional);
	}

	@XmlAttribute
	public String getNewType() {
		return OptionalHelper.optionalToString(newTypeOptional);
	}

	@Override
	@XmlAttribute
	public JApiChangeStatus getChangeStatus() {
		return changeStatus;
	}

	@XmlTransient
	public Optional<ClassType> getOldTypeOptional() {
		return oldTypeOptional;
	}

	@XmlTransient
	public Optional<ClassType> getNewTypeOptional() {
		return newTypeOptional;
	}
}
