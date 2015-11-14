package model;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import http.ConnectionOperator;

public class ChatsModel implements Updated {
	
	@Override
	public void update(Object... params) {
		for (ChatModel CM : chatModels) {
			CM.getLock();
			CM.update();
			CM.releaseLock();
		}
	}

	private ArrayList<ChatModel> chatModels = new ArrayList<>();
	
	public ArrayList<ChatModel> getChatModels() {
		return chatModels;
	}
	
	@Override
	public void getLock() {
		log.info("Waiting for lock: "+Thread.currentThread().getName());
		lock.lock();
		log.info("Got lock: "+Thread.currentThread().getName());

	}

	@Override
	public void releaseLock() {
		log.info("Releasing lock: "+ Thread.currentThread().getName());
		lock.unlock();
		
	}
	
	private Lock lock = new ReentrantLock();
	private static ConnectionOperator CO = new ConnectionOperator(1000);
	
	private static Logger log = Logger.getAnonymousLogger();
	static {
		log.setLevel(Level.ALL);
	}
	
	public static ConnectionOperator getConnectionOperator() {
		return CO;
	}

}
