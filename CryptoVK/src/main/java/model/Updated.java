package model;

public interface Updated {
	public void update(Object... params);
	public void getLock(String takerName);
	public void releaseLock(String takerName);
}
