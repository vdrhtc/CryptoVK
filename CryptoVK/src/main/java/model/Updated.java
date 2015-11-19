package model;

public interface Updated {
	public void update(Object... params);
	public void getLock();
	public void releaseLock();
}
