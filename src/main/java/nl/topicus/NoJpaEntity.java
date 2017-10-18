package nl.topicus;

public class NoJpaEntity
{
	private long id;

	private long version;

	private int value;

	public NoJpaEntity(long id, long version, int value)
	{
		this.id = id;
		this.version = version;
		this.value = value;
	}

	public long getId()
	{
		return id;
	}

	public long getVersion()
	{
		return version;
	}

	public int getValue()
	{
		return value;
	}
}
