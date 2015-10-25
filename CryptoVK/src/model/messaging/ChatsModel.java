package model.messaging;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.Updated;

public class ChatsModel implements Updated {
	
	@Override
	public void update() {
		for (ChatModel CM : chatModels) {
			CM.update();
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
	
	private static Logger log = Logger.getAnonymousLogger();
	static {
		log.setLevel(Level.ALL);
	}

}
