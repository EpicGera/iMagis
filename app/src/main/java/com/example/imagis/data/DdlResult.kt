package com.example.imagis.data

import java.io.Serializable

/**
 * Represents a single Direct Download Link result from DDL sites (e.g. Pahe).
 * Unlike TorrentResult (which has magnets), DDL results point to file hosting services.
 * The downloadUrl typically goes through link shorteners before reaching the final host.
 */
data class DdlResult(
    val title: String,        // e.g. "Inception (2010)"
    val quality: String,      // e.g. "1080p x264", "720p x265"
    val sizeDisplay: String,  // e.g. "2.1 GB", "900 MB"
    val host: String,         // Short code: "PD", "MG", "GD", "VF", "1F", "1D"
    val hostFullName: String, // Full name: "PixelDrain", "Mega", "GDrive", etc.
    val downloadUrl: String,  // pahe.ink/process.php?d=... redirect URL
    val source: String = "Pahe"
) : Serializable
