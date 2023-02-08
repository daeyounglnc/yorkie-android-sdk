package dev.yorkie.document.crdt

import dev.yorkie.document.json.escapeString
import dev.yorkie.document.time.ActorID
import dev.yorkie.document.time.TimeTicket

/**
 * The value passed as an argument to [CrdtText.onChanges].
 * [CrdtText.onChanges] is called when the [CrdtText] is modified.
 */
public data class TextChange(
    val type: TextChangeType,
    val actor: ActorID,
    val from: Int,
    val to: Int,
    val content: String? = null,
    val attributes: Map<String, String>? = null,
)

/**
 * The type of [TextChange].
 */
public enum class TextChangeType {
    Content, Selection, Style
}

/**
 * Represents the selection of text range in the editor.
 */
internal data class Selection(
    val from: RgaTreeSplitNodePos,
    val to: RgaTreeSplitNodePos,
    val executedAt: TimeTicket,
)

internal data class TextValue(
    val content: String,
    private val _attributes: Rht = Rht(),
) : RgaTreeSplitValue<TextValue> {

    val attributes
        get() = _attributes.nodeKeyValueMap

    val attributesWithTimeTicket
        get() = _attributes.toList()

    override val length: Int by content::length

    override fun get(index: Int): Char = content[index]

    override fun deepCopy(): TextValue {
        return copy(_attributes = _attributes.deepCopy())
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return TextValue(content.substring(startIndex, endIndex), _attributes.deepCopy())
    }

    fun setAttribute(key: String, value: String, executedAt: TimeTicket) {
        _attributes.set(key, value, executedAt)
    }

    fun toJson(): String {
        val attrs = _attributes.nodeKeyValueMap.entries.joinToString(",") {
            """"${it.key}":"${escapeString(it.value)}""""
        }
        return if (attrs.isEmpty()) {
            """{"val":"${escapeString(content)}"}"""
        } else {
            """{"attrs":{$attrs},"val":"${escapeString(content)}"}"""
        }
    }

    override fun toString(): String {
        return content
    }
}