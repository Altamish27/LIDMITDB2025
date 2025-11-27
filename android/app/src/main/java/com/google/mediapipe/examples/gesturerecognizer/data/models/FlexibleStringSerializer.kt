package com.google.mediapipe.examples.gesturerecognizer.data.models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.long
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull

/**
 * Serializer yang dapat membaca nilai string maupun numeric dan mengubahnya ke String.
 * Berguna untuk field user_id yang kadang dikirim sebagai number.
 */
object FlexibleStringSerializer : KSerializer<String> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("FlexibleStringSerializer", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String {
        if (decoder is JsonDecoder) {
            val element = decoder.decodeJsonElement()
            return when {
                element is JsonPrimitive && element.isString -> element.content
                element is JsonPrimitive && element.longOrNull != null -> element.long.toString()
                element is JsonPrimitive && element.doubleOrNull != null -> element.double.toString()
                element is JsonObject -> {
                    val nestedCode = element["code"]?.jsonPrimitive?.contentOrNull
                    nestedCode ?: element.toString()
                }
                else -> element.toString()
            }
        }
        return decoder.decodeString()
    }

    override fun serialize(encoder: Encoder, value: String) {
        encoder.encodeString(value)
    }
}

