package dev.yorkie.api

import dev.yorkie.api.v1.OperationKt.add
import dev.yorkie.api.v1.OperationKt.increase
import dev.yorkie.api.v1.OperationKt.move
import dev.yorkie.api.v1.OperationKt.remove
import dev.yorkie.api.v1.OperationKt.set
import dev.yorkie.api.v1.operation
import dev.yorkie.document.operation.AddOperation
import dev.yorkie.document.operation.IncreaseOperation
import dev.yorkie.document.operation.MoveOperation
import dev.yorkie.document.operation.Operation
import dev.yorkie.document.operation.RemoveOperation
import dev.yorkie.document.operation.SetOperation

internal typealias PBOperation = dev.yorkie.api.v1.Operation

internal fun List<PBOperation>.toOperations(): List<Operation> {
    return map {
        when {
            it.hasSet() -> SetOperation(
                key = it.set.key,
                value = it.set.value.toCrdtElement(),
                parentCreatedAt = it.set.parentCreatedAt.toTimeTicket(),
                executedAt = it.set.executedAt.toTimeTicket(),
            )
            it.hasAdd() -> AddOperation(
                parentCreatedAt = it.add.parentCreatedAt.toTimeTicket(),
                prevCreatedAt = it.add.prevCreatedAt.toTimeTicket(),
                value = it.add.value.toCrdtElement(),
                executedAt = it.add.executedAt.toTimeTicket(),
            )
            it.hasMove() -> MoveOperation(
                parentCreatedAt = it.move.parentCreatedAt.toTimeTicket(),
                prevCreatedAt = it.move.prevCreatedAt.toTimeTicket(),
                createdAt = it.move.createdAt.toTimeTicket(),
                executedAt = it.move.executedAt.toTimeTicket(),
            )
            it.hasRemove() -> RemoveOperation(
                parentCreatedAt = it.remove.parentCreatedAt.toTimeTicket(),
                createdAt = it.remove.createdAt.toTimeTicket(),
                executedAt = it.remove.executedAt.toTimeTicket(),
            )
            it.hasIncrease() -> IncreaseOperation(
                parentCreatedAt = it.increase.parentCreatedAt.toTimeTicket(),
                executedAt = it.increase.executedAt.toTimeTicket(),
                value = it.increase.value.toCrdtElement(),
            )
            it.hasEdit() -> TODO("not yet implemented")
            it.hasSelect() -> TODO("not yet implemented")
            it.hasRichEdit() -> TODO("not yet implemented")
            it.hasStyle() -> TODO("not yet implemented")
            else -> error("unimplemented operation")
        }
    }
}

// TODO(7hong13): should check Edit, Select, RichEdit, Style Operations
internal fun Operation.toPBOperation(): PBOperation {
    return when (val operation = this@toPBOperation) {
        is SetOperation -> {
            operation {
                set = set {
                    parentCreatedAt = operation.parentCreatedAt.toPBTimeTicket()
                    key = operation.key
                    value = operation.value.toPBJsonElementSimple()
                    executedAt = operation.executedAt.toPBTimeTicket()
                }
            }
        }
        is AddOperation -> {
            operation {
                add = add {
                    parentCreatedAt = operation.parentCreatedAt.toPBTimeTicket()
                    prevCreatedAt = operation.prevCreatedAt.toPBTimeTicket()
                    value = operation.value.toPBJsonElementSimple()
                    executedAt = operation.executedAt.toPBTimeTicket()
                }
            }
        }
        is MoveOperation -> {
            operation {
                move = move {
                    parentCreatedAt = operation.parentCreatedAt.toPBTimeTicket()
                    prevCreatedAt = operation.prevCreatedAt.toPBTimeTicket()
                    createdAt = operation.createdAt.toPBTimeTicket()
                    executedAt = operation.executedAt.toPBTimeTicket()
                }
            }
        }
        is RemoveOperation -> {
            operation {
                remove = remove {
                    parentCreatedAt = operation.parentCreatedAt.toPBTimeTicket()
                    createdAt = operation.createdAt.toPBTimeTicket()
                    executedAt = operation.executedAt.toPBTimeTicket()
                }
            }
        }
        is IncreaseOperation -> {
            operation {
                increase = increase {
                    parentCreatedAt = operation.parentCreatedAt.toPBTimeTicket()
                    value = operation.value.toPBJsonElementSimple()
                    executedAt = operation.executedAt.toPBTimeTicket()
                }
            }
        }
        else -> error("unimplemented operation $operation")
    }
}

internal fun List<Operation>.toPBOperations(): List<PBOperation> {
    return map { it.toPBOperation() }
}