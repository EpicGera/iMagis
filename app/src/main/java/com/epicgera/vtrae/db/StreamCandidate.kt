package com.epicgera.vtrae.db

/**
 * Lightweight data class for Room query projection.
 * Used by ChannelDao.findStreamCandidates() for relevance scoring.
 */
data class StreamCandidate(
    val lowercaseName: String,
    val streamUrl: String
)

