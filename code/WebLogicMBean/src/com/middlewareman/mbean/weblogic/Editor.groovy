/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic

class Editor {

	static Editor getValidateOnly() {
		new Editor(action:Action.ValidateOnly)
	}

	static Editor getSaveOnly() {
		new Editor(action:Action.SaveOnly)
	}

	static Editor getActivate() {
		new Editor(action:Action.Activate)
	}

	enum Action {
		/** Only validate changes without saving. */
		ValidateOnly,
		/** Only save changes (includes validating) without activating. */
		SaveOnly,
		/** Activate any unactivated changes. */
		Activate
	}

	/** Start a new edit session if your user already has one. */
	boolean alwaysStart = true

	/** 
	 * Prevents other uses from starting an edit session until waitTimeInMillis 
	 * expires, edits are activated, or the edit session is stopped. 
	 * If the value of waitTimeInMillis is 0 and an edit session is active, 
	 * this operation returns immediately. 
	 * To block indefinitely, specify a value of -1.
	 */
	Integer editWaitTime = 0

	/**
	 * Specifies the number of milliseconds after which the lock on the 
	 * configuration is no longer guaranteed. This time out is enforced lazily. 
	 * If no other user starts an edit session after the timeout expires, 
	 * the unsaved changes are left intact and may be saved. 
	 * If another user starts an edit session after the timeout expires, 
	 * unsaved changes are automatically reverted and the lock is given to that 
	 * new user. Specify a value of -1 to indicate that you do not want the 
	 * edit to time out. In this case, if you do not stop your edit session, 
	 * only an administrator can stop the edit session by invoking the 
	 * cancelEdit operation.
	 */
	Integer editTimeout = 60000

	/**
	 * Specifies whether the edit session should be exclusive. An edit session 
	 * will cause a subsequent call to startEdit by the same owner to wait 
	 * until the edit session lock is released.
	 */
	Boolean editExclusive = true

	Action action

	/**
	 * Number of milliseconds for the operation to complete. 
	 * If the elapsed time exceeds that value, then the activation of the 
	 * configuration changes will be cancelled. 
	 * If -1, then the activation will not timeout. If a non-zero timeout is 
	 * specified, then the activate will wait until the activate operation has 
	 * completed or until the timeout period has elapsed. If a zero timeout is 
	 * specified, then the activate will return immediately and the caller can 
	 * wait for completion using the ActivationTaskMBean.
	 */
	Long activateTimeout = -1

	/** Always stop any active edit session before returning. */
	boolean alwaysStop = true

	def editDomain(def editService, Closure script) {
		assert editService.Name == 'EditService'
		def configManager = editService.ConfigurationManager
		// TODO cancelEdit if somebody else's
		// TODO undoUnactivatedChanges
		def domain = (configManager.Editor && !alwaysStart) ?
				editService.DomainConfiguration :
				configManager.startEdit(editWaitTime,editTimeout,editExclusive)
		assert domain
		assert domain.Type == 'Domain'
		try {
			// LOG
			script domain
			// LOG

			switch (action) {
				case Action.ValidateOnly:
					configManager.undo()
					break
				case Action.SaveOnly:
					configManager.save()
					break
				case Action.Activate:
					configManager.save()
					return configManager.activate(activateTimeout)
				default:
					break
			}
		} catch(Exception e) {
			// TODO Log
			throw e
		} finally {
			if (alwaysStop && configManager.Editor) {
				// TODO Log
				try {
					configManager.stopEdit()
				} catch(Exception e) {
					// TODO log
				}
			}
			// TODO Log done
		}
	}
}
