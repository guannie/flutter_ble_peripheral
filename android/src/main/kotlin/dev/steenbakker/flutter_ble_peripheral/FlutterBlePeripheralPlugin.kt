/*
 * Copyright (c) 2020. Julian Steenbakker.
 * All rights reserved. Use of this source code is governed by a
 * BSD-style license that can be found in the LICENSE file.
 */

package dev.steenbakker.flutter_ble_peripheral

import android.content.Context
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.*

class FlutterBlePeripheralPlugin: FlutterPlugin, MethodChannel.MethodCallHandler, EventChannel.StreamHandler {

  private var applicationContext: Context? = null
  private var methodChannel: MethodChannel? = null
  private var eventChannel: EventChannel? = null
  private var peripheral: Peripheral? = null
  private var eventSink: EventChannel.EventSink? = null

  /** Plugin registration embedding v1 */
  companion object {
    @JvmStatic
    fun registerWith(registrar: PluginRegistry.Registrar) {
      FlutterBlePeripheralPlugin().onAttachedToEngine(registrar.context(), registrar.messenger())
    }
  }

  /** Plugin registration embedding v2 */
  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    onAttachedToEngine(flutterPluginBinding.applicationContext, flutterPluginBinding.binaryMessenger)
  }

  private fun onAttachedToEngine(applicationContext: Context, messenger: BinaryMessenger) {
    this.applicationContext = applicationContext
    methodChannel = MethodChannel(messenger, "dev.steenbakker.flutter_ble_peripheral/ble_state")
    eventChannel = EventChannel(messenger, "dev.steenbakker.flutter_ble_peripheral/ble_event")
    methodChannel!!.setMethodCallHandler(this)
    eventChannel!!.setStreamHandler(this)
    peripheral = Peripheral()
    peripheral!!.init(applicationContext)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    applicationContext = null
    methodChannel!!.setMethodCallHandler(null)
    methodChannel = null
    eventChannel!!.setStreamHandler(null)
    eventChannel = null
    peripheral = null
  }

  // TODO: Add different functions
  // TODO: Add permission check
  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
    when (call.method) {
      "start" -> startPeripheral(call, result)
      "stop" -> stopPeripheral(result)
      "isAdvertising" -> result.success(peripheral!!.isAdvertising())
//      "isTransmissionSupported" -> isTransmissionSupported(result)
      else -> result.notImplemented()
    }
  }

  private fun startPeripheral(call: MethodCall, result: MethodChannel.Result) {
    if (call.arguments !is Map<*, *>) {
      throw IllegalArgumentException("Arguments are not a map! " + call.arguments)
    }

    val arguments = call.arguments as Map<String, Any>

    // TODO: Add arguments for advertising
    val beaconData = Data(
            arguments["uuid"] as String
//            arguments["transmissionPower"] as Int?,
//            arguments["manufacturerId"] as Int?
    )

    peripheral!!.start(beaconData)
    result.success(null)
  }

  private fun stopPeripheral(result: MethodChannel.Result) {
    peripheral!!.stop()
    result.success(null)
  }

  // TODO: Fix listeners
  override fun onListen(event: Any?, eventSink: EventChannel.EventSink) {
    this.eventSink = eventSink
  }

  override fun onCancel(event: Any?) {
    this.eventSink = null
  }
}

