package com.example.ime

import android.inputmethodservice.InputMethodService
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

abstract class ComposeInputMethodService : InputMethodService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private val lifecycleRegistry by lazy { LifecycleRegistry(this) }
    private val store by lazy { ViewModelStore() }
    private val savedStateRegistryController by lazy { SavedStateRegistryController.create(this) }

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        try {
            if (!savedStateRegistry.isRestored) {
                savedStateRegistryController.performRestore(Bundle())
            }
        } catch (e: Throwable) {
            Log.w("ComposeIME", "performRestore ignored: ${e.message}")
        }
        dispatchLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onStartInput(attribute: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        if (lifecycleRegistry.currentState == Lifecycle.State.CREATED) {
            dispatchLifecycleEvent(Lifecycle.Event.ON_START)
        }
    }

    override fun onStartInputView(info: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        if (lifecycleRegistry.currentState == Lifecycle.State.CREATED) {
            dispatchLifecycleEvent(Lifecycle.Event.ON_START)
        }
        dispatchLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onWindowShown() {
        super.onWindowShown()
        if (lifecycleRegistry.currentState == Lifecycle.State.CREATED) {
            dispatchLifecycleEvent(Lifecycle.Event.ON_START)
        }
        if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            dispatchLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            dispatchLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        }
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            dispatchLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        }
        if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            dispatchLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            dispatchLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
        dispatchLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        try {
            store.clear()
        } catch (e: Throwable) {
            Log.w("ComposeIME", "store clear error: ${e.message}")
        }
    }

    private fun dispatchLifecycleEvent(event: Lifecycle.Event) {
        try {
            lifecycleRegistry.handleLifecycleEvent(event)
        } catch (e: Throwable) {
            Log.w("ComposeIME", "Lifecycle transition error on $event: ${e.message}")
        }
    }
}

