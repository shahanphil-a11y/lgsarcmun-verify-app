package pk.lgsarcmun.hub

/** Admin membership actions. Actual writes must be performed by a protected Supabase function/RLS policy. */
interface AdminRepository {
    suspend fun revokeMembership(memberId: String): Result<Unit>
    suspend fun restoreMembership(memberId: String): Result<Unit>
}

class MembershipAdminRepository : AdminRepository {
    override suspend fun revokeMembership(memberId: String): Result<Unit> = Result.failure(UnsupportedOperationException("Connect to protected Supabase admin endpoint"))
    override suspend fun restoreMembership(memberId: String): Result<Unit> = Result.failure(UnsupportedOperationException("Connect to protected Supabase admin endpoint"))
}
