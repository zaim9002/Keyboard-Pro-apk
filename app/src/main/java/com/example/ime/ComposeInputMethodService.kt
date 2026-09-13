package com.example.ime

import android.inputmethodservice.InputMethodService
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

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

    override fun onEvaluateFullscreenMode(): Boolean = false

    override fun onStartInput(attribute: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        ensureStarted()
    }

    override fun onStartInputView(info: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        ensureResumed()
    }

    override fun onWindowShown() {
        super.onWindowShown()
        ensureResumed()
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        ensurePaused()
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        ensureStopped()
    }

    override fun onDestroy() {
        super.onDestroy()
        ensureStopped()
        dispatchLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        try {
            store.clear()
        } catch (e: Throwable) {
            Log.w("ComposeIME", "store clear error: ${e.message}")
        }
    }

    private fun ensureStarted() {
        if (lifecycleRegistry.currentState == Lifecycle.State.INITIALIZED) {
            dispatchLifecycleEvent(Lifecycle.Event.ON_CREATE)
        }
        if (lifecycleRegistry.currentState == Lifecycle.State.CREATED) {
            dispatchLifecycleEvent(Lifecycle.Event.ON_START)
        }
    }

    private fun ensureResumed() {
        ensureStarted()
        if (lifecycleRegistry.currentState == Lifecycle.State.STARTED) {
            dispatchLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
    }

    private fun ensurePaused() {
        if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            dispatchLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        }
    }

    private fun ensureStopped() {
        ensurePaused()
        if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            dispatchLifecycleEvent(Lifecycle.Event.ON_STOP)
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

