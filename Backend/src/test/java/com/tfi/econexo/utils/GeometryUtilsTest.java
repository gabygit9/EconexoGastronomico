package com.tfi.econexo.utils;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;

import static org.junit.jupiter.api.Assertions.*;

class GeometryUtilsTest {

    @Test
    void createPointSRID4326_WhenLatitudeAndLongitudeAreCorrect() {
        Point point = GeometryUtils.createPoint(12.34, 56.78);

        assertNotNull(point);
        assertEquals(12.34, point.getCoordinate().x);
        assertEquals(56.78, point.getCoordinate().y);
        assertEquals(4326, point.getSRID());
    }

    @Test
    void createPoint_WhenLatitudeAndLongitudeAreNull_ReturnNull(){
        Point point = GeometryUtils.createPoint(null, null);

        assertNull(point);
    }

}