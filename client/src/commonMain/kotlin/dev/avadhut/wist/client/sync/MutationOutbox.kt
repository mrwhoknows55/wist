package dev.avadhut.wist.client.sync

interface MutationOutbox {
    suspend fun enqueue(mutation: PendingMutation)

    suspend fun pendingCount(): Int
}

object NoOpMutationOutbox : MutationOutbox {
    override suspend fun enqueue(mutation: PendingMutation) = when (mutation) {
        is PendingMutation.AddItem -> println("[Wist] NoOpMutationOutbox: enqueue ignored type=${mutation}")
        is PendingMutation.CreateWishlist -> println("[Wist] NoOpMutationOutbox: enqueue ignored type=${mutation}")
        is PendingMutation.DeleteItem -> println("[Wist] NoOpMutationOutbox: enqueue ignored type=${mutation}")
        is PendingMutation.DeleteWishlist -> println("[Wist] NoOpMutationOutbox: enqueue ignored type=${mutation}")
        is PendingMutation.UpdateItem -> println("[Wist] NoOpMutationOutbox: enqueue ignored type=${mutation}")
        is PendingMutation.UpdateWishlist -> println("[Wist] NoOpMutationOutbox: enqueue ignored type=${mutation}")
    }

    override suspend fun pendingCount(): Int = 0
}
