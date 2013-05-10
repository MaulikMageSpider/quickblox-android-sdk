package com.quickblox.chat_v2.apis;

import java.util.ArrayList;
import java.util.Collection;

import org.jivesoftware.smack.packet.Presence;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.quickblox.chat_v2.core.ChatApplication;
import com.quickblox.module.chat.QBChat;
import com.quickblox.module.chat.model.QBChatRoster.QBRosterListener;
import com.quickblox.module.chat.xmpp.SubscriptionListener;
import com.quickblox.module.users.model.QBUser;

public class RosterManager implements QBRosterListener, SubscriptionListener {
	
	private ArrayList<String> subscribes;
	private ArrayList<String> userIds;
	
	private Context context;
	private ChatApplication app;
	
	private int userID;
	
	public RosterManager(Context context) {
		this.context = context;
		QBChat.startAutoSendPresence(30);
		subscribes = new ArrayList<String>();
		app = ChatApplication.getInstance();
	}
	
	@Override
	public void entriesAdded(Collection<Integer> addedEntriesIds) {
	}
	
	@Override
	public void entriesDeleted(Collection<Integer> deletedEntriesIds) {
		System.out.println("entress deleted = " + deletedEntriesIds.toString());
		
	}
	
	@Override
	public void entriesUpdated(Collection<Integer> updatedEntriesIds) {
		System.out.println("entress updated = " + updatedEntriesIds.toString());
		
	}
	
	@Override
	public void presenceChanged(Presence presence) {
		System.out.println("presence = " + presence.toString());
		
	}
	
	@Override
	public void onSubscribe(int userId) {
		
		subscribes.add(String.valueOf(userId));
		((Activity) context).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				ChatApplication.getInstance().getQbm().getQbUsersInfoCandidate(subscribes);
				
			}
		});
		
	}
	
	@Override
	public void onUnSubscribe(int userId) {
		System.out.println("Отписался");
		for (QBUser user : app.getContactsList()) {
			if (user.getId() == userId) {
				app.getContactsList().remove(user);
				refreshContactList();
			}
		}
	}
	
	public void sendRequestToSubscribe(int userId) {
		userID = userId;
		((Activity) context).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				QBChat.subscribed(userID);
				refreshContactList();
			}
		});
	}
	
	public void sendRequestToUnSubscribe(int userId) {
		
		userID = userId;
		((Activity) context).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				QBChat.unsubscribed(userID);
				refreshContactList();
			}
		});
	}
	
	public void refreshContactList() {
		userIds = new ArrayList<String>();
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				
				((Activity) context).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if (app.getQbRoster().getUsersId() != null) {
							for (Integer in : app.getQbRoster().getUsersId()) {
								userIds.add(String.valueOf(in));
							}
							System.out.println("Отправка контактов на проверку = " + userIds.size());
							app.getQbm().getQbUsersInfoContact(userIds);
						}
					}
				});
			}
		}, 5000);
		
	}
}
