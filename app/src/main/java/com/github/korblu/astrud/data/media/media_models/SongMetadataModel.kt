package com.github.korblu.astrud.data.media.media_models

data class SongMetadataModel(
    val title : String?,
    val artist : String?,
    val album : String?,
    val uri : String?,
    val albumArtUri : String?,
    val duration : Long?,
    val year : Long?
)
