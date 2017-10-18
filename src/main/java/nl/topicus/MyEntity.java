package nl.topicus;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

@Entity()
public class MyEntity
{
	@Id()
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_sequence")
	@SequenceGenerator(name = "id_sequence", sequenceName = "id_sequence", allocationSize = 100,
			initialValue = 100)
	@Access(AccessType.PROPERTY)
	private Long id;

	@Version
	@Column(nullable = false)
	private long version;

	@Column(nullable = false)
	private int value;

	public MyEntity()
	{
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public long getVersion()
	{
		return version;
	}

	public void setVersion(long version)
	{
		this.version = version;
	}

	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		this.value = value;
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ": " + String.valueOf(getId());
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj instanceof MyEntity)
		{
			MyEntity other = (MyEntity) obj;
			if (getId() == null && other.getId() == null)
				return false;
			else if (getId() != null)
				return getId().equals(other.getId());
			// else fallthrough
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		if (getId() != null)
			return getId().hashCode();
		return super.hashCode();
	}
}
