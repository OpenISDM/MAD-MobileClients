package com.example.maplisttest;


import org.osmdroid.ResourceProxy.string;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;

/******************************************************************************
 *  Class name: GoogleOffLineXYTileSource
 *  Inheritance: N/A
 *  Methods: N/A
 *  Functionality: Use osmdroid library to get googlemaps tile resource 
******************************************************************************/
public class GoogleOffLineXYTileSource extends OnlineTileSourceBase 
{
    public GoogleOffLineXYTileSource(final String layerName,
            final string layerResourceId, final int layerZoomMinLevel,
            final int layerZoomMaxLevel, final int layerTileSizePixels,
            final String layerImageFilenameEnding, final String... aBaseUrl) 
    {
        super(layerName, layerResourceId, layerZoomMinLevel, layerZoomMaxLevel,
                layerTileSizePixels, layerImageFilenameEnding, aBaseUrl);
    }

    @Override
    public String getTileURLString(MapTile aTile) 
    {
        return getBaseUrl() + "&x=" + aTile.getX() + "&y="+ aTile.getY() +
        		"&z=" + aTile.getZoomLevel();
    }

}
