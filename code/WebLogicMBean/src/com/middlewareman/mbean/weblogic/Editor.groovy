/*
 * $Id$
 * Copyright (c) 2011 Middlewareman Limited. All rights reserved.
 */
package com.middlewareman.mbean.weblogic

import java.util.logging.Level
import java.util.logging.Logger

import com.middlewareman.groovy.util.StackTraceCleaner

/**
 * Facade for ConfigurationManager to edit domain configuration with a closure.
 * Editor provides all the boilerplate functionality for starting and closing
 * the edit session.
 *  
 * @author Andreas Nyberg
 */
class Editor {

	private static final Logger logger = Logger.getLogger(Editor.class.name)
	private static final className = Editor.class.name

	/** 
	 * New instance that only validates and then undoes all changes without saving,
	 * useful for testing scripts without saving any changes.
	 */
	static Editor getValidateOnly() {
		new Editor(action:Action.ValidateOnly)
	}

	/**
	 * New instance that only validates and saves any changes, but does not 
	 * activate them. This allows reviewing the changes through the admin
	 * console before activating them from there.
	 */
	static Editor getSaveOnly() {
		new Editor(action:Action.SaveOnly)
	}

	/**
	 * New instance that validates, saves and activates any changes.
	 */
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

	/** Undo any existing unsaved changes. */
	boolean undoExistingChanges = true

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
		final methodName = 'editDomain'
		assert editService.Name == 'EditService'

		def configManager = editService.ConfigurationManager

		// TODO cancelEdit if somebody else's session
		// TODO undoUnactivatedChanges

		try {
			def domain
			if (configManager.Editor && !alwaysStart) {
				logger.logp Level.FINE, className, methodName, 'Reusing your old edit session'
				domain = editService.DomainConfiguration
			} else {
				logger.logp Level.FINE, className, methodName, 'Starting a new edit session'
				domain = configManager.startEdit(editWaitTime,editTimeout,editExclusive)
			}
			assert domain
			assert domain.Type == 'Domain'

			def changes = configManager.Changes
			if (changes && undoExistingChanges) {
				logger.logp Level.INFO, className, methodName, "undoing ${changes.size()} existing unsaved changes"
				configManager.undo()
			}
			assert !configManager.Changes

			logger.logp Level.FINE, className, methodName, "Starting configuration script on ${domain.@home}"
			script domain
			logger.logp Level.FINE, className, methodName, 'Finished configuration script'
			switch (action) {
				case Action.ValidateOnly:
					logger.logp Level.FINE, className, methodName, 'Validating only'
					configManager.validate()
					logger.logp Level.FINE, className, methodName, 'Undoing'
					configManager.undo()
					break
				case Action.SaveOnly:
					logger.logp Level.FINE, className, methodName, 'Saving only'
					configManager.save()
					break
				case Action.Activate:
					logger.logp Level.FINE, className, methodName, 'Saving before activate'
					configManager.save()
					logger.logp Level.FINE, className, methodName, 'Activating'
					return configManager.activate(activateTimeout)
				default:
					assert false, action
					break
			}
			logger.logp Level.FINE, className, methodName, 'Done.'
		} catch(Exception e) {
			new StackTraceCleaner().deepClean e
			logger.logp Level.WARNING, className, methodName, "Unable to complete edit on ${domain.@home}", e
			throw e
		} finally {
			if (alwaysStop && configManager.Editor) {
				def newchanges = configManager.Changes.size()
				if (newchanges)
					logger.logp Level.INFO, className, methodName, "Stopping edit session: discarding $newchanges unsaved changes."
				else
					logger.logp Level.FINE, className, methodName, 'Stopping edit session without any changes'
				try {
					configManager.stopEdit()
				} catch(Exception e) {
					new StackTraceCleaner().deepClean e
					logger.logp Level.ERROR, className, methodName, 'Unable to stop edit session', e
				}
			}
		}
	}
}
