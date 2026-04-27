/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
$(document).ready(function() {

    $(".click-title").mouseenter( function(    e){
        e.preventDefault();
        this.style.cursor="pointer";
    });
    $(".click-title").mousedown( function(event){
        event.preventDefault();
    });

    // Ugly code while this script is shared among several pages
    try{
        refreshHitsPerSecond(true);
    } catch(e){}
    try{
        refreshResponseTimeOverTime(true);
    } catch(e){}
    try{
        refreshResponseTimePercentiles();
    } catch(e){}
});


var responseTimePercentilesInfos = {
        data: {"result": {"minY": 255.0, "minX": 0.0, "maxY": 10469.0, "series": [{"data": [[0.0, 255.0], [0.1, 255.0], [0.2, 255.0], [0.3, 255.0], [0.4, 297.0], [0.5, 297.0], [0.6, 297.0], [0.7, 316.0], [0.8, 316.0], [0.9, 316.0], [1.0, 323.0], [1.1, 323.0], [1.2, 323.0], [1.3, 336.0], [1.4, 336.0], [1.5, 336.0], [1.6, 370.0], [1.7, 370.0], [1.8, 370.0], [1.9, 488.0], [2.0, 488.0], [2.1, 488.0], [2.2, 535.0], [2.3, 535.0], [2.4, 535.0], [2.5, 549.0], [2.6, 549.0], [2.7, 549.0], [2.8, 549.0], [2.9, 557.0], [3.0, 557.0], [3.1, 557.0], [3.2, 581.0], [3.3, 581.0], [3.4, 581.0], [3.5, 613.0], [3.6, 613.0], [3.7, 613.0], [3.8, 621.0], [3.9, 621.0], [4.0, 621.0], [4.1, 645.0], [4.2, 645.0], [4.3, 645.0], [4.4, 650.0], [4.5, 650.0], [4.6, 650.0], [4.7, 713.0], [4.8, 713.0], [4.9, 713.0], [5.0, 744.0], [5.1, 744.0], [5.2, 744.0], [5.3, 744.0], [5.4, 805.0], [5.5, 805.0], [5.6, 805.0], [5.7, 892.0], [5.8, 892.0], [5.9, 892.0], [6.0, 896.0], [6.1, 896.0], [6.2, 896.0], [6.3, 1037.0], [6.4, 1037.0], [6.5, 1037.0], [6.6, 1058.0], [6.7, 1058.0], [6.8, 1058.0], [6.9, 1339.0], [7.0, 1339.0], [7.1, 1339.0], [7.2, 1453.0], [7.3, 1453.0], [7.4, 1453.0], [7.5, 1485.0], [7.6, 1485.0], [7.7, 1485.0], [7.8, 1485.0], [7.9, 1512.0], [8.0, 1512.0], [8.1, 1512.0], [8.2, 1547.0], [8.3, 1547.0], [8.4, 1547.0], [8.5, 1583.0], [8.6, 1583.0], [8.7, 1583.0], [8.8, 1619.0], [8.9, 1619.0], [9.0, 1619.0], [9.1, 1620.0], [9.2, 1620.0], [9.3, 1620.0], [9.4, 1675.0], [9.5, 1675.0], [9.6, 1675.0], [9.7, 1680.0], [9.8, 1680.0], [9.9, 1680.0], [10.0, 1716.0], [10.1, 1716.0], [10.2, 1716.0], [10.3, 1716.0], [10.4, 1760.0], [10.5, 1760.0], [10.6, 1760.0], [10.7, 1761.0], [10.8, 1761.0], [10.9, 1761.0], [11.0, 1762.0], [11.1, 1762.0], [11.2, 1762.0], [11.3, 1765.0], [11.4, 1765.0], [11.5, 1765.0], [11.6, 1801.0], [11.7, 1801.0], [11.8, 1801.0], [11.9, 1828.0], [12.0, 1828.0], [12.1, 1828.0], [12.2, 1834.0], [12.3, 1834.0], [12.4, 1834.0], [12.5, 1852.0], [12.6, 1852.0], [12.7, 1852.0], [12.8, 1852.0], [12.9, 1860.0], [13.0, 1860.0], [13.1, 1860.0], [13.2, 1873.0], [13.3, 1873.0], [13.4, 1873.0], [13.5, 1886.0], [13.6, 1886.0], [13.7, 1886.0], [13.8, 1906.0], [13.9, 1906.0], [14.0, 1906.0], [14.1, 1907.0], [14.2, 1907.0], [14.3, 1907.0], [14.4, 1925.0], [14.5, 1925.0], [14.6, 1925.0], [14.7, 1930.0], [14.8, 1930.0], [14.9, 1930.0], [15.0, 1936.0], [15.1, 1936.0], [15.2, 1936.0], [15.3, 1936.0], [15.4, 1944.0], [15.5, 1944.0], [15.6, 1944.0], [15.7, 1954.0], [15.8, 1954.0], [15.9, 1954.0], [16.0, 1961.0], [16.1, 1961.0], [16.2, 1961.0], [16.3, 1974.0], [16.4, 1974.0], [16.5, 1974.0], [16.6, 1974.0], [16.7, 1974.0], [16.8, 1974.0], [16.9, 1977.0], [17.0, 1977.0], [17.1, 1977.0], [17.2, 2006.0], [17.3, 2006.0], [17.4, 2006.0], [17.5, 2016.0], [17.6, 2016.0], [17.7, 2016.0], [17.8, 2016.0], [17.9, 2032.0], [18.0, 2032.0], [18.1, 2032.0], [18.2, 2041.0], [18.3, 2041.0], [18.4, 2041.0], [18.5, 2068.0], [18.6, 2068.0], [18.7, 2068.0], [18.8, 2088.0], [18.9, 2088.0], [19.0, 2088.0], [19.1, 2094.0], [19.2, 2094.0], [19.3, 2094.0], [19.4, 2098.0], [19.5, 2098.0], [19.6, 2098.0], [19.7, 2102.0], [19.8, 2102.0], [19.9, 2102.0], [20.0, 2108.0], [20.1, 2108.0], [20.2, 2108.0], [20.3, 2108.0], [20.4, 2111.0], [20.5, 2111.0], [20.6, 2111.0], [20.7, 2118.0], [20.8, 2118.0], [20.9, 2118.0], [21.0, 2144.0], [21.1, 2144.0], [21.2, 2144.0], [21.3, 2144.0], [21.4, 2144.0], [21.5, 2144.0], [21.6, 2145.0], [21.7, 2145.0], [21.8, 2145.0], [21.9, 2175.0], [22.0, 2175.0], [22.1, 2175.0], [22.2, 2209.0], [22.3, 2209.0], [22.4, 2209.0], [22.5, 2255.0], [22.6, 2255.0], [22.7, 2255.0], [22.8, 2255.0], [22.9, 2260.0], [23.0, 2260.0], [23.1, 2260.0], [23.2, 2280.0], [23.3, 2280.0], [23.4, 2280.0], [23.5, 2295.0], [23.6, 2295.0], [23.7, 2295.0], [23.8, 2392.0], [23.9, 2392.0], [24.0, 2392.0], [24.1, 2561.0], [24.2, 2561.0], [24.3, 2561.0], [24.4, 2595.0], [24.5, 2595.0], [24.6, 2595.0], [24.7, 2660.0], [24.8, 2660.0], [24.9, 2660.0], [25.0, 2700.0], [25.1, 2700.0], [25.2, 2700.0], [25.3, 2700.0], [25.4, 3115.0], [25.5, 3115.0], [25.6, 3115.0], [25.7, 3524.0], [25.8, 3524.0], [25.9, 3524.0], [26.0, 3552.0], [26.1, 3552.0], [26.2, 3552.0], [26.3, 3748.0], [26.4, 3748.0], [26.5, 3748.0], [26.6, 3919.0], [26.7, 3919.0], [26.8, 3919.0], [26.9, 4122.0], [27.0, 4122.0], [27.1, 4122.0], [27.2, 4202.0], [27.3, 4202.0], [27.4, 4202.0], [27.5, 4292.0], [27.6, 4292.0], [27.7, 4292.0], [27.8, 4292.0], [27.9, 4512.0], [28.0, 4512.0], [28.1, 4512.0], [28.2, 4523.0], [28.3, 4523.0], [28.4, 4523.0], [28.5, 4571.0], [28.6, 4571.0], [28.7, 4571.0], [28.8, 4765.0], [28.9, 4765.0], [29.0, 4765.0], [29.1, 4800.0], [29.2, 4800.0], [29.3, 4800.0], [29.4, 4826.0], [29.5, 4826.0], [29.6, 4826.0], [29.7, 4866.0], [29.8, 4866.0], [29.9, 4866.0], [30.0, 4872.0], [30.1, 4872.0], [30.2, 4872.0], [30.3, 4872.0], [30.4, 4914.0], [30.5, 4914.0], [30.6, 4914.0], [30.7, 4917.0], [30.8, 4917.0], [30.9, 4917.0], [31.0, 4939.0], [31.1, 4939.0], [31.2, 4939.0], [31.3, 4960.0], [31.4, 4960.0], [31.5, 4960.0], [31.6, 5009.0], [31.7, 5009.0], [31.8, 5009.0], [31.9, 5075.0], [32.0, 5075.0], [32.1, 5075.0], [32.2, 5094.0], [32.3, 5094.0], [32.4, 5094.0], [32.5, 5148.0], [32.6, 5148.0], [32.7, 5148.0], [32.8, 5148.0], [32.9, 5149.0], [33.0, 5149.0], [33.1, 5149.0], [33.2, 5157.0], [33.3, 5157.0], [33.4, 5157.0], [33.5, 5189.0], [33.6, 5189.0], [33.7, 5189.0], [33.8, 5190.0], [33.9, 5190.0], [34.0, 5190.0], [34.1, 5192.0], [34.2, 5192.0], [34.3, 5192.0], [34.4, 5268.0], [34.5, 5268.0], [34.6, 5268.0], [34.7, 5288.0], [34.8, 5288.0], [34.9, 5288.0], [35.0, 5293.0], [35.1, 5293.0], [35.2, 5293.0], [35.3, 5293.0], [35.4, 5303.0], [35.5, 5303.0], [35.6, 5303.0], [35.7, 5304.0], [35.8, 5304.0], [35.9, 5304.0], [36.0, 5314.0], [36.1, 5314.0], [36.2, 5314.0], [36.3, 5345.0], [36.4, 5345.0], [36.5, 5345.0], [36.6, 5349.0], [36.7, 5349.0], [36.8, 5349.0], [36.9, 5368.0], [37.0, 5368.0], [37.1, 5368.0], [37.2, 5391.0], [37.3, 5391.0], [37.4, 5391.0], [37.5, 5398.0], [37.6, 5398.0], [37.7, 5398.0], [37.8, 5398.0], [37.9, 5419.0], [38.0, 5419.0], [38.1, 5419.0], [38.2, 5441.0], [38.3, 5441.0], [38.4, 5441.0], [38.5, 5497.0], [38.6, 5497.0], [38.7, 5497.0], [38.8, 5497.0], [38.9, 5497.0], [39.0, 5497.0], [39.1, 5513.0], [39.2, 5513.0], [39.3, 5513.0], [39.4, 5521.0], [39.5, 5521.0], [39.6, 5521.0], [39.7, 5525.0], [39.8, 5525.0], [39.9, 5525.0], [40.0, 5530.0], [40.1, 5530.0], [40.2, 5530.0], [40.3, 5530.0], [40.4, 5533.0], [40.5, 5533.0], [40.6, 5533.0], [40.7, 5546.0], [40.8, 5546.0], [40.9, 5546.0], [41.0, 5553.0], [41.1, 5553.0], [41.2, 5553.0], [41.3, 5571.0], [41.4, 5571.0], [41.5, 5571.0], [41.6, 5617.0], [41.7, 5617.0], [41.8, 5617.0], [41.9, 5630.0], [42.0, 5630.0], [42.1, 5630.0], [42.2, 5660.0], [42.3, 5660.0], [42.4, 5660.0], [42.5, 5663.0], [42.6, 5663.0], [42.7, 5663.0], [42.8, 5663.0], [42.9, 5675.0], [43.0, 5675.0], [43.1, 5675.0], [43.2, 5702.0], [43.3, 5702.0], [43.4, 5702.0], [43.5, 5705.0], [43.6, 5705.0], [43.7, 5705.0], [43.8, 5710.0], [43.9, 5710.0], [44.0, 5710.0], [44.1, 5725.0], [44.2, 5725.0], [44.3, 5725.0], [44.4, 5739.0], [44.5, 5739.0], [44.6, 5739.0], [44.7, 5773.0], [44.8, 5773.0], [44.9, 5773.0], [45.0, 5803.0], [45.1, 5803.0], [45.2, 5803.0], [45.3, 5803.0], [45.4, 5813.0], [45.5, 5813.0], [45.6, 5813.0], [45.7, 5831.0], [45.8, 5831.0], [45.9, 5831.0], [46.0, 5832.0], [46.1, 5832.0], [46.2, 5832.0], [46.3, 5862.0], [46.4, 5862.0], [46.5, 5862.0], [46.6, 5881.0], [46.7, 5881.0], [46.8, 5881.0], [46.9, 5886.0], [47.0, 5886.0], [47.1, 5886.0], [47.2, 5911.0], [47.3, 5911.0], [47.4, 5911.0], [47.5, 5959.0], [47.6, 5959.0], [47.7, 5959.0], [47.8, 5959.0], [47.9, 5976.0], [48.0, 5976.0], [48.1, 5976.0], [48.2, 5978.0], [48.3, 5978.0], [48.4, 5978.0], [48.5, 5979.0], [48.6, 5979.0], [48.7, 5979.0], [48.8, 5980.0], [48.9, 5980.0], [49.0, 5980.0], [49.1, 5989.0], [49.2, 5989.0], [49.3, 5989.0], [49.4, 5995.0], [49.5, 5995.0], [49.6, 5995.0], [49.7, 6002.0], [49.8, 6002.0], [49.9, 6002.0], [50.0, 6011.0], [50.1, 6011.0], [50.2, 6011.0], [50.3, 6011.0], [50.4, 6060.0], [50.5, 6060.0], [50.6, 6060.0], [50.7, 6067.0], [50.8, 6067.0], [50.9, 6067.0], [51.0, 6076.0], [51.1, 6076.0], [51.2, 6076.0], [51.3, 6097.0], [51.4, 6097.0], [51.5, 6097.0], [51.6, 6106.0], [51.7, 6106.0], [51.8, 6106.0], [51.9, 6106.0], [52.0, 6106.0], [52.1, 6106.0], [52.2, 6112.0], [52.3, 6112.0], [52.4, 6112.0], [52.5, 6121.0], [52.6, 6121.0], [52.7, 6121.0], [52.8, 6121.0], [52.9, 6169.0], [53.0, 6169.0], [53.1, 6169.0], [53.2, 6191.0], [53.3, 6191.0], [53.4, 6191.0], [53.5, 6195.0], [53.6, 6195.0], [53.7, 6195.0], [53.8, 6196.0], [53.9, 6196.0], [54.0, 6196.0], [54.1, 6230.0], [54.2, 6230.0], [54.3, 6230.0], [54.4, 6236.0], [54.5, 6236.0], [54.6, 6236.0], [54.7, 6239.0], [54.8, 6239.0], [54.9, 6239.0], [55.0, 6259.0], [55.1, 6259.0], [55.2, 6259.0], [55.3, 6259.0], [55.4, 6289.0], [55.5, 6289.0], [55.6, 6289.0], [55.7, 6291.0], [55.8, 6291.0], [55.9, 6291.0], [56.0, 6313.0], [56.1, 6313.0], [56.2, 6313.0], [56.3, 6363.0], [56.4, 6363.0], [56.5, 6363.0], [56.6, 6365.0], [56.7, 6365.0], [56.8, 6365.0], [56.9, 6379.0], [57.0, 6379.0], [57.1, 6379.0], [57.2, 6393.0], [57.3, 6393.0], [57.4, 6393.0], [57.5, 6414.0], [57.6, 6414.0], [57.7, 6414.0], [57.8, 6414.0], [57.9, 6431.0], [58.0, 6431.0], [58.1, 6431.0], [58.2, 6454.0], [58.3, 6454.0], [58.4, 6454.0], [58.5, 6488.0], [58.6, 6488.0], [58.7, 6488.0], [58.8, 6502.0], [58.9, 6502.0], [59.0, 6502.0], [59.1, 6512.0], [59.2, 6512.0], [59.3, 6512.0], [59.4, 6544.0], [59.5, 6544.0], [59.6, 6544.0], [59.7, 6559.0], [59.8, 6559.0], [59.9, 6559.0], [60.0, 6560.0], [60.1, 6560.0], [60.2, 6560.0], [60.3, 6560.0], [60.4, 6567.0], [60.5, 6567.0], [60.6, 6567.0], [60.7, 6569.0], [60.8, 6569.0], [60.9, 6569.0], [61.0, 6592.0], [61.1, 6592.0], [61.2, 6592.0], [61.3, 6617.0], [61.4, 6617.0], [61.5, 6617.0], [61.6, 6659.0], [61.7, 6659.0], [61.8, 6659.0], [61.9, 6686.0], [62.0, 6686.0], [62.1, 6686.0], [62.2, 6693.0], [62.3, 6693.0], [62.4, 6693.0], [62.5, 6710.0], [62.6, 6710.0], [62.7, 6710.0], [62.8, 6710.0], [62.9, 6720.0], [63.0, 6720.0], [63.1, 6720.0], [63.2, 6748.0], [63.3, 6748.0], [63.4, 6748.0], [63.5, 6758.0], [63.6, 6758.0], [63.7, 6758.0], [63.8, 6763.0], [63.9, 6763.0], [64.0, 6763.0], [64.1, 6767.0], [64.2, 6767.0], [64.3, 6767.0], [64.4, 6769.0], [64.5, 6769.0], [64.6, 6769.0], [64.7, 6784.0], [64.8, 6784.0], [64.9, 6784.0], [65.0, 6799.0], [65.1, 6799.0], [65.2, 6799.0], [65.3, 6799.0], [65.4, 6829.0], [65.5, 6829.0], [65.6, 6829.0], [65.7, 6842.0], [65.8, 6842.0], [65.9, 6842.0], [66.0, 6869.0], [66.1, 6869.0], [66.2, 6869.0], [66.3, 6901.0], [66.4, 6901.0], [66.5, 6901.0], [66.6, 6906.0], [66.7, 6906.0], [66.8, 6906.0], [66.9, 6951.0], [67.0, 6951.0], [67.1, 6951.0], [67.2, 6962.0], [67.3, 6962.0], [67.4, 6962.0], [67.5, 6962.0], [67.6, 6962.0], [67.7, 6962.0], [67.8, 6962.0], [67.9, 6968.0], [68.0, 6968.0], [68.1, 6968.0], [68.2, 6978.0], [68.3, 6978.0], [68.4, 6978.0], [68.5, 6980.0], [68.6, 6980.0], [68.7, 6980.0], [68.8, 6987.0], [68.9, 6987.0], [69.0, 6987.0], [69.1, 6990.0], [69.2, 6990.0], [69.3, 6990.0], [69.4, 6999.0], [69.5, 6999.0], [69.6, 6999.0], [69.7, 7001.0], [69.8, 7001.0], [69.9, 7001.0], [70.0, 7010.0], [70.1, 7010.0], [70.2, 7010.0], [70.3, 7010.0], [70.4, 7028.0], [70.5, 7028.0], [70.6, 7028.0], [70.7, 7040.0], [70.8, 7040.0], [70.9, 7040.0], [71.0, 7055.0], [71.1, 7055.0], [71.2, 7055.0], [71.3, 7064.0], [71.4, 7064.0], [71.5, 7064.0], [71.6, 7073.0], [71.7, 7073.0], [71.8, 7073.0], [71.9, 7084.0], [72.0, 7084.0], [72.1, 7084.0], [72.2, 7089.0], [72.3, 7089.0], [72.4, 7089.0], [72.5, 7124.0], [72.6, 7124.0], [72.7, 7124.0], [72.8, 7124.0], [72.9, 7146.0], [73.0, 7146.0], [73.1, 7146.0], [73.2, 7161.0], [73.3, 7161.0], [73.4, 7161.0], [73.5, 7193.0], [73.6, 7193.0], [73.7, 7193.0], [73.8, 7200.0], [73.9, 7200.0], [74.0, 7200.0], [74.1, 7219.0], [74.2, 7219.0], [74.3, 7219.0], [74.4, 7227.0], [74.5, 7227.0], [74.6, 7227.0], [74.7, 7231.0], [74.8, 7231.0], [74.9, 7231.0], [75.0, 7235.0], [75.1, 7235.0], [75.2, 7235.0], [75.3, 7235.0], [75.4, 7240.0], [75.5, 7240.0], [75.6, 7240.0], [75.7, 7262.0], [75.8, 7262.0], [75.9, 7262.0], [76.0, 7263.0], [76.1, 7263.0], [76.2, 7263.0], [76.3, 7282.0], [76.4, 7282.0], [76.5, 7282.0], [76.6, 7285.0], [76.7, 7285.0], [76.8, 7285.0], [76.9, 7314.0], [77.0, 7314.0], [77.1, 7314.0], [77.2, 7314.0], [77.3, 7314.0], [77.4, 7314.0], [77.5, 7384.0], [77.6, 7384.0], [77.7, 7384.0], [77.8, 7384.0], [77.9, 7422.0], [78.0, 7422.0], [78.1, 7422.0], [78.2, 7462.0], [78.3, 7462.0], [78.4, 7462.0], [78.5, 7466.0], [78.6, 7466.0], [78.7, 7466.0], [78.8, 7500.0], [78.9, 7500.0], [79.0, 7500.0], [79.1, 7582.0], [79.2, 7582.0], [79.3, 7582.0], [79.4, 7603.0], [79.5, 7603.0], [79.6, 7603.0], [79.7, 7662.0], [79.8, 7662.0], [79.9, 7662.0], [80.0, 7717.0], [80.1, 7717.0], [80.2, 7717.0], [80.3, 7717.0], [80.4, 7799.0], [80.5, 7799.0], [80.6, 7799.0], [80.7, 7837.0], [80.8, 7837.0], [80.9, 7837.0], [81.0, 7891.0], [81.1, 7891.0], [81.2, 7891.0], [81.3, 7916.0], [81.4, 7916.0], [81.5, 7916.0], [81.6, 8007.0], [81.7, 8007.0], [81.8, 8007.0], [81.9, 8062.0], [82.0, 8062.0], [82.1, 8062.0], [82.2, 8102.0], [82.3, 8102.0], [82.4, 8102.0], [82.5, 8104.0], [82.6, 8104.0], [82.7, 8104.0], [82.8, 8104.0], [82.9, 8295.0], [83.0, 8295.0], [83.1, 8295.0], [83.2, 8311.0], [83.3, 8311.0], [83.4, 8311.0], [83.5, 8387.0], [83.6, 8387.0], [83.7, 8387.0], [83.8, 8401.0], [83.9, 8401.0], [84.0, 8401.0], [84.1, 8457.0], [84.2, 8457.0], [84.3, 8457.0], [84.4, 8488.0], [84.5, 8488.0], [84.6, 8488.0], [84.7, 8506.0], [84.8, 8506.0], [84.9, 8506.0], [85.0, 8612.0], [85.1, 8612.0], [85.2, 8612.0], [85.3, 8612.0], [85.4, 8637.0], [85.5, 8637.0], [85.6, 8637.0], [85.7, 8638.0], [85.8, 8638.0], [85.9, 8638.0], [86.0, 8647.0], [86.1, 8647.0], [86.2, 8647.0], [86.3, 8684.0], [86.4, 8684.0], [86.5, 8684.0], [86.6, 8780.0], [86.7, 8780.0], [86.8, 8780.0], [86.9, 8789.0], [87.0, 8789.0], [87.1, 8789.0], [87.2, 8802.0], [87.3, 8802.0], [87.4, 8802.0], [87.5, 8889.0], [87.6, 8889.0], [87.7, 8889.0], [87.8, 8889.0], [87.9, 8926.0], [88.0, 8926.0], [88.1, 8926.0], [88.2, 8944.0], [88.3, 8944.0], [88.4, 8944.0], [88.5, 8978.0], [88.6, 8978.0], [88.7, 8978.0], [88.8, 9041.0], [88.9, 9041.0], [89.0, 9041.0], [89.1, 9054.0], [89.2, 9054.0], [89.3, 9054.0], [89.4, 9059.0], [89.5, 9059.0], [89.6, 9059.0], [89.7, 9134.0], [89.8, 9134.0], [89.9, 9134.0], [90.0, 9231.0], [90.1, 9231.0], [90.2, 9231.0], [90.3, 9231.0], [90.4, 9250.0], [90.5, 9250.0], [90.6, 9250.0], [90.7, 9272.0], [90.8, 9272.0], [90.9, 9272.0], [91.0, 9328.0], [91.1, 9328.0], [91.2, 9328.0], [91.3, 9396.0], [91.4, 9396.0], [91.5, 9396.0], [91.6, 9487.0], [91.7, 9487.0], [91.8, 9487.0], [91.9, 9516.0], [92.0, 9516.0], [92.1, 9516.0], [92.2, 9524.0], [92.3, 9524.0], [92.4, 9524.0], [92.5, 9526.0], [92.6, 9526.0], [92.7, 9526.0], [92.8, 9526.0], [92.9, 9533.0], [93.0, 9533.0], [93.1, 9533.0], [93.2, 9546.0], [93.3, 9546.0], [93.4, 9546.0], [93.5, 9589.0], [93.6, 9589.0], [93.7, 9589.0], [93.8, 9593.0], [93.9, 9593.0], [94.0, 9593.0], [94.1, 9621.0], [94.2, 9621.0], [94.3, 9621.0], [94.4, 9654.0], [94.5, 9654.0], [94.6, 9654.0], [94.7, 9666.0], [94.8, 9666.0], [94.9, 9666.0], [95.0, 9681.0], [95.1, 9681.0], [95.2, 9681.0], [95.3, 9681.0], [95.4, 9697.0], [95.5, 9697.0], [95.6, 9697.0], [95.7, 9700.0], [95.8, 9700.0], [95.9, 9700.0], [96.0, 9719.0], [96.1, 9719.0], [96.2, 9719.0], [96.3, 9730.0], [96.4, 9730.0], [96.5, 9730.0], [96.6, 9732.0], [96.7, 9732.0], [96.8, 9732.0], [96.9, 9777.0], [97.0, 9777.0], [97.1, 9777.0], [97.2, 9812.0], [97.3, 9812.0], [97.4, 9812.0], [97.5, 9868.0], [97.6, 9868.0], [97.7, 9868.0], [97.8, 9868.0], [97.9, 9908.0], [98.0, 9908.0], [98.1, 9908.0], [98.2, 9966.0], [98.3, 9966.0], [98.4, 9966.0], [98.5, 10048.0], [98.6, 10048.0], [98.7, 10048.0], [98.8, 10071.0], [98.9, 10071.0], [99.0, 10071.0], [99.1, 10085.0], [99.2, 10085.0], [99.3, 10085.0], [99.4, 10144.0], [99.5, 10144.0], [99.6, 10144.0], [99.7, 10469.0], [99.8, 10469.0], [99.9, 10469.0]], "isOverall": false, "label": "GET /api/demo/counter/race-condition", "isController": false}], "supportsControllersDiscrimination": true, "maxX": 100.0, "title": "Response Time Percentiles"}},
        getOptions: function() {
            return {
                series: {
                    points: { show: false }
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimePercentiles'
                },
                xaxis: {
                    tickDecimals: 1,
                    axisLabel: "Percentiles",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Percentile value in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : %x.2 percentile was %y ms"
                },
                selection: { mode: "xy" },
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesResponseTimePercentiles"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimesPercentiles"), dataset, options);
            // setup overview
            $.plot($("#overviewResponseTimesPercentiles"), dataset, prepareOverviewOptions(options));
        }
};

/**
 * @param elementId Id of element where we display message
 */
function setEmptyGraph(elementId) {
    $(function() {
        $(elementId).text("No graph series with filter="+seriesFilter);
    });
}

// Response times percentiles
function refreshResponseTimePercentiles() {
    var infos = responseTimePercentilesInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyResponseTimePercentiles");
        return;
    }
    if (isGraph($("#flotResponseTimesPercentiles"))){
        infos.createGraph();
    } else {
        var choiceContainer = $("#choicesResponseTimePercentiles");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimesPercentiles", "#overviewResponseTimesPercentiles");
        $('#bodyResponseTimePercentiles .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
}

var responseTimeDistributionInfos = {
        data: {"result": {"minY": 1.0, "minX": 200.0, "maxY": 11.0, "series": [{"data": [[600.0, 4.0], [700.0, 2.0], [800.0, 3.0], [1000.0, 2.0], [1300.0, 1.0], [1400.0, 2.0], [1500.0, 3.0], [1600.0, 4.0], [1700.0, 5.0], [1800.0, 7.0], [1900.0, 11.0], [2000.0, 8.0], [2100.0, 8.0], [2200.0, 5.0], [2300.0, 1.0], [2500.0, 2.0], [2600.0, 1.0], [2700.0, 1.0], [3100.0, 1.0], [3500.0, 2.0], [3700.0, 1.0], [3900.0, 1.0], [4200.0, 2.0], [4100.0, 1.0], [4500.0, 3.0], [4700.0, 1.0], [4800.0, 4.0], [5000.0, 3.0], [5100.0, 6.0], [4900.0, 4.0], [5300.0, 8.0], [5200.0, 3.0], [5400.0, 4.0], [5600.0, 5.0], [5500.0, 8.0], [5700.0, 6.0], [5800.0, 7.0], [5900.0, 8.0], [6100.0, 8.0], [6000.0, 6.0], [6200.0, 6.0], [6300.0, 5.0], [6600.0, 4.0], [6500.0, 8.0], [6400.0, 4.0], [6900.0, 11.0], [6700.0, 9.0], [6800.0, 3.0], [7000.0, 9.0], [7100.0, 4.0], [7200.0, 10.0], [7400.0, 3.0], [7300.0, 3.0], [7500.0, 2.0], [7600.0, 2.0], [7900.0, 1.0], [7800.0, 2.0], [7700.0, 2.0], [8000.0, 2.0], [8100.0, 2.0], [8600.0, 5.0], [8300.0, 2.0], [8200.0, 1.0], [8400.0, 3.0], [8500.0, 1.0], [8700.0, 2.0], [8900.0, 3.0], [9000.0, 3.0], [9100.0, 1.0], [8800.0, 2.0], [9200.0, 3.0], [9300.0, 2.0], [9500.0, 7.0], [9600.0, 5.0], [9400.0, 1.0], [9700.0, 5.0], [10000.0, 3.0], [9900.0, 2.0], [9800.0, 2.0], [10100.0, 1.0], [10400.0, 1.0], [200.0, 2.0], [300.0, 4.0], [400.0, 1.0], [500.0, 4.0]], "isOverall": false, "label": "GET /api/demo/counter/race-condition", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 100, "maxX": 10400.0, "title": "Response Time Distribution"}},
        getOptions: function() {
            var granularity = this.data.result.granularity;
            return {
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimeDistribution'
                },
                xaxis:{
                    axisLabel: "Response times in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of responses",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                bars : {
                    show: true,
                    barWidth: this.data.result.granularity
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: function(label, xval, yval, flotItem){
                        return yval + " responses for " + label + " were between " + xval + " and " + (xval + granularity) + " ms";
                    }
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimeDistribution"), prepareData(data.result.series, $("#choicesResponseTimeDistribution")), options);
        }

};

// Response time distribution
function refreshResponseTimeDistribution() {
    var infos = responseTimeDistributionInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyResponseTimeDistribution");
        return;
    }
    if (isGraph($("#flotResponseTimeDistribution"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesResponseTimeDistribution");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        $('#footerResponseTimeDistribution .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};


var syntheticResponseTimeDistributionInfos = {
        data: {"result": {"minY": 7.0, "minX": 0.0, "ticks": [[0, "Requests having \nresponse time <= 500ms"], [1, "Requests having \nresponse time > 500ms and <= 1,500ms"], [2, "Requests having \nresponse time > 1,500ms"], [3, "Requests in error"]], "maxY": 295.0, "series": [{"data": [[0.0, 7.0]], "color": "#9ACD32", "isOverall": false, "label": "Requests having \nresponse time <= 500ms", "isController": false}, {"data": [[1.0, 18.0]], "color": "yellow", "isOverall": false, "label": "Requests having \nresponse time > 500ms and <= 1,500ms", "isController": false}, {"data": [[2.0, 295.0]], "color": "orange", "isOverall": false, "label": "Requests having \nresponse time > 1,500ms", "isController": false}, {"data": [], "color": "#FF6347", "isOverall": false, "label": "Requests in error", "isController": false}], "supportsControllersDiscrimination": false, "maxX": 2.0, "title": "Synthetic Response Times Distribution"}},
        getOptions: function() {
            return {
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendSyntheticResponseTimeDistribution'
                },
                xaxis:{
                    axisLabel: "Response times ranges",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                    tickLength:0,
                    min:-0.5,
                    max:3.5
                },
                yaxis: {
                    axisLabel: "Number of responses",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                bars : {
                    show: true,
                    align: "center",
                    barWidth: 0.25,
                    fill:.75
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: function(label, xval, yval, flotItem){
                        return yval + " " + label;
                    }
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var options = this.getOptions();
            prepareOptions(options, data);
            options.xaxis.ticks = data.result.ticks;
            $.plot($("#flotSyntheticResponseTimeDistribution"), prepareData(data.result.series, $("#choicesSyntheticResponseTimeDistribution")), options);
        }

};

// Response time distribution
function refreshSyntheticResponseTimeDistribution() {
    var infos = syntheticResponseTimeDistributionInfos;
    prepareSeries(infos.data, true);
    if (isGraph($("#flotSyntheticResponseTimeDistribution"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesSyntheticResponseTimeDistribution");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        $('#footerSyntheticResponseTimeDistribution .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var activeThreadsOverTimeInfos = {
        data: {"result": {"minY": 48.04687500000002, "minX": 1.77688284E12, "maxY": 48.04687500000002, "series": [{"data": [[1.77688284E12, 48.04687500000002]], "isOverall": false, "label": "64 Users x 5 Iterations", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.77688284E12, "title": "Active Threads Over Time"}},
        getOptions: function() {
            return {
                series: {
                    stack: true,
                    lines: {
                        show: true,
                        fill: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of active threads",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 6,
                    show: true,
                    container: '#legendActiveThreadsOverTime'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                selection: {
                    mode: 'xy'
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : At %x there were %y active threads"
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesActiveThreadsOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotActiveThreadsOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewActiveThreadsOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Active Threads Over Time
function refreshActiveThreadsOverTime(fixTimestamps) {
    var infos = activeThreadsOverTimeInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotActiveThreadsOverTime"))) {
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesActiveThreadsOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotActiveThreadsOverTime", "#overviewActiveThreadsOverTime");
        $('#footerActiveThreadsOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var timeVsThreadsInfos = {
        data: {"result": {"minY": 306.5, "minX": 1.0, "maxY": 9426.42857142857, "series": [{"data": [[2.0, 1760.0], [4.0, 2234.0], [5.0, 1936.0], [6.0, 306.5], [7.0, 1085.5], [8.0, 1860.0], [9.0, 1217.0], [10.0, 1925.0], [11.0, 2108.0], [12.0, 2041.0], [13.0, 1873.0], [14.0, 737.6666666666667], [15.0, 970.3333333333333], [16.0, 1048.75], [18.0, 1890.0], [19.0, 1128.0], [20.0, 1675.0], [21.0, 1203.0], [22.0, 2144.0], [23.0, 1709.6666666666665], [24.0, 2595.0], [25.0, 2660.0], [26.0, 7262.0], [28.0, 7050.25], [29.0, 7305.5], [30.0, 6941.5], [31.0, 7340.5], [33.0, 6259.0], [32.0, 7193.0], [34.0, 3392.333333333333], [35.0, 6708.0], [37.0, 5917.4], [36.0, 6765.0], [39.0, 4718.6], [38.0, 7021.0], [40.0, 5031.0], [41.0, 4118.25], [42.0, 3453.0], [43.0, 3810.666666666667], [44.0, 3513.8333333333335], [45.0, 3450.6250000000005], [46.0, 2477.125], [47.0, 5388.666666666667], [48.0, 4167.333333333333], [49.0, 4789.833333333333], [51.0, 4209.0], [50.0, 8802.0], [53.0, 6447.090909090909], [52.0, 6947.833333333333], [55.0, 6748.714285714285], [54.0, 6259.5], [57.0, 5963.333333333333], [56.0, 6330.150000000001], [58.0, 6432.071428571428], [59.0, 9426.42857142857], [61.0, 8935.714285714286], [60.0, 9148.666666666664], [63.0, 5722.399999999999], [62.0, 7323.133333333333], [64.0, 5380.071428571428], [1.0, 2209.0]], "isOverall": false, "label": "GET /api/demo/counter/race-condition", "isController": false}, {"data": [[48.04687500000002, 5558.340625000004]], "isOverall": false, "label": "GET /api/demo/counter/race-condition-Aggregated", "isController": false}], "supportsControllersDiscrimination": true, "maxX": 64.0, "title": "Time VS Threads"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    axisLabel: "Number of active threads",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average response times in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: { noColumns: 2,show: true, container: '#legendTimeVsThreads' },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s: At %x.2 active threads, Average response time was %y.2 ms"
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesTimeVsThreads"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotTimesVsThreads"), dataset, options);
            // setup overview
            $.plot($("#overviewTimesVsThreads"), dataset, prepareOverviewOptions(options));
        }
};

// Time vs threads
function refreshTimeVsThreads(){
    var infos = timeVsThreadsInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyTimeVsThreads");
        return;
    }
    if(isGraph($("#flotTimesVsThreads"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesTimeVsThreads");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotTimesVsThreads", "#overviewTimesVsThreads");
        $('#footerTimeVsThreads .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var bytesThroughputOverTimeInfos = {
        data : {"result": {"minY": 784.0, "minX": 1.77688284E12, "maxY": 2618.0666666666666, "series": [{"data": [[1.77688284E12, 2618.0666666666666]], "isOverall": false, "label": "Bytes received per second", "isController": false}, {"data": [[1.77688284E12, 784.0]], "isOverall": false, "label": "Bytes sent per second", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.77688284E12, "title": "Bytes Throughput Over Time"}},
        getOptions : function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity) ,
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Bytes / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendBytesThroughputOverTime'
                },
                selection: {
                    mode: "xy"
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y"
                }
            };
        },
        createGraph : function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesBytesThroughputOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotBytesThroughputOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewBytesThroughputOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Bytes throughput Over Time
function refreshBytesThroughputOverTime(fixTimestamps) {
    var infos = bytesThroughputOverTimeInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotBytesThroughputOverTime"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesBytesThroughputOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotBytesThroughputOverTime", "#overviewBytesThroughputOverTime");
        $('#footerBytesThroughputOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
}

var responseTimesOverTimeInfos = {
        data: {"result": {"minY": 5558.340625000004, "minX": 1.77688284E12, "maxY": 5558.340625000004, "series": [{"data": [[1.77688284E12, 5558.340625000004]], "isOverall": false, "label": "GET /api/demo/counter/race-condition", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.77688284E12, "title": "Response Time Over Time"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average response time in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimesOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Average response time was %y ms"
                }
            };
        },
        createGraph: function() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesResponseTimesOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimesOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewResponseTimesOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Response Times Over Time
function refreshResponseTimeOverTime(fixTimestamps) {
    var infos = responseTimesOverTimeInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyResponseTimeOverTime");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotResponseTimesOverTime"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesResponseTimesOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimesOverTime", "#overviewResponseTimesOverTime");
        $('#footerResponseTimesOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var latenciesOverTimeInfos = {
        data: {"result": {"minY": 5557.574999999996, "minX": 1.77688284E12, "maxY": 5557.574999999996, "series": [{"data": [[1.77688284E12, 5557.574999999996]], "isOverall": false, "label": "GET /api/demo/counter/race-condition", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.77688284E12, "title": "Latencies Over Time"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average response latencies in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendLatenciesOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Average latency was %y ms"
                }
            };
        },
        createGraph: function () {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesLatenciesOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotLatenciesOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewLatenciesOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Latencies Over Time
function refreshLatenciesOverTime(fixTimestamps) {
    var infos = latenciesOverTimeInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyLatenciesOverTime");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotLatenciesOverTime"))) {
        infos.createGraph();
    }else {
        var choiceContainer = $("#choicesLatenciesOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotLatenciesOverTime", "#overviewLatenciesOverTime");
        $('#footerLatenciesOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var connectTimeOverTimeInfos = {
        data: {"result": {"minY": 0.6, "minX": 1.77688284E12, "maxY": 0.6, "series": [{"data": [[1.77688284E12, 0.6]], "isOverall": false, "label": "GET /api/demo/counter/race-condition", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.77688284E12, "title": "Connect Time Over Time"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getConnectTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Average Connect Time in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendConnectTimeOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Average connect time was %y ms"
                }
            };
        },
        createGraph: function () {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesConnectTimeOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotConnectTimeOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewConnectTimeOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Connect Time Over Time
function refreshConnectTimeOverTime(fixTimestamps) {
    var infos = connectTimeOverTimeInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyConnectTimeOverTime");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotConnectTimeOverTime"))) {
        infos.createGraph();
    }else {
        var choiceContainer = $("#choicesConnectTimeOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotConnectTimeOverTime", "#overviewConnectTimeOverTime");
        $('#footerConnectTimeOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var responseTimePercentilesOverTimeInfos = {
        data: {"result": {"minY": 255.0, "minX": 1.77688284E12, "maxY": 10469.0, "series": [{"data": [[1.77688284E12, 10469.0]], "isOverall": false, "label": "Max", "isController": false}, {"data": [[1.77688284E12, 255.0]], "isOverall": false, "label": "Min", "isController": false}, {"data": [[1.77688284E12, 9221.300000000003]], "isOverall": false, "label": "90th percentile", "isController": false}, {"data": [[1.77688284E12, 10082.06]], "isOverall": false, "label": "99th percentile", "isController": false}, {"data": [[1.77688284E12, 6006.5]], "isOverall": false, "label": "Median", "isController": false}, {"data": [[1.77688284E12, 9680.25]], "isOverall": false, "label": "95th percentile", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.77688284E12, "title": "Response Time Percentiles Over Time (successful requests only)"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true,
                        fill: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Response Time in ms",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: '#legendResponseTimePercentilesOverTime'
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s : at %x Response time was %y ms"
                }
            };
        },
        createGraph: function () {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesResponseTimePercentilesOverTime"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotResponseTimePercentilesOverTime"), dataset, options);
            // setup overview
            $.plot($("#overviewResponseTimePercentilesOverTime"), dataset, prepareOverviewOptions(options));
        }
};

// Response Time Percentiles Over Time
function refreshResponseTimePercentilesOverTime(fixTimestamps) {
    var infos = responseTimePercentilesOverTimeInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotResponseTimePercentilesOverTime"))) {
        infos.createGraph();
    }else {
        var choiceContainer = $("#choicesResponseTimePercentilesOverTime");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimePercentilesOverTime", "#overviewResponseTimePercentilesOverTime");
        $('#footerResponseTimePercentilesOverTime .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};


var responseTimeVsRequestInfos = {
    data: {"result": {"minY": 1219.5, "minX": 1.0, "maxY": 9526.0, "series": [{"data": [[8.0, 1945.5], [2.0, 8186.5], [33.0, 6784.0], [9.0, 5441.0], [36.0, 5562.0], [39.0, 6617.0], [11.0, 6969.0], [12.0, 2276.5], [4.0, 1219.5], [1.0, 4765.0], [17.0, 6097.0], [5.0, 2618.0], [21.0, 9526.0], [23.0, 1936.0], [6.0, 7153.5], [26.0, 5143.5], [7.0, 7603.0]], "isOverall": false, "label": "Successes", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 1000, "maxX": 39.0, "title": "Response Time Vs Request"}},
    getOptions: function() {
        return {
            series: {
                lines: {
                    show: false
                },
                points: {
                    show: true
                }
            },
            xaxis: {
                axisLabel: "Global number of requests per second",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            yaxis: {
                axisLabel: "Median Response Time in ms",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            legend: {
                noColumns: 2,
                show: true,
                container: '#legendResponseTimeVsRequest'
            },
            selection: {
                mode: 'xy'
            },
            grid: {
                hoverable: true // IMPORTANT! this is needed for tooltip to work
            },
            tooltip: true,
            tooltipOpts: {
                content: "%s : Median response time at %x req/s was %y ms"
            },
            colors: ["#9ACD32", "#FF6347"]
        };
    },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesResponseTimeVsRequest"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotResponseTimeVsRequest"), dataset, options);
        // setup overview
        $.plot($("#overviewResponseTimeVsRequest"), dataset, prepareOverviewOptions(options));

    }
};

// Response Time vs Request
function refreshResponseTimeVsRequest() {
    var infos = responseTimeVsRequestInfos;
    prepareSeries(infos.data);
    if (isGraph($("#flotResponseTimeVsRequest"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesResponseTimeVsRequest");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotResponseTimeVsRequest", "#overviewResponseTimeVsRequest");
        $('#footerResponseRimeVsRequest .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};


var latenciesVsRequestInfos = {
    data: {"result": {"minY": 1219.0, "minX": 1.0, "maxY": 9524.0, "series": [{"data": [[8.0, 1944.0], [2.0, 8186.0], [33.0, 6784.0], [9.0, 5441.0], [36.0, 5561.5], [39.0, 6616.0], [11.0, 6969.0], [12.0, 2275.0], [4.0, 1219.0], [1.0, 4764.0], [17.0, 6096.0], [5.0, 2618.0], [21.0, 9524.0], [23.0, 1935.0], [6.0, 7153.5], [26.0, 5139.0], [7.0, 7602.0]], "isOverall": false, "label": "Successes", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 1000, "maxX": 39.0, "title": "Latencies Vs Request"}},
    getOptions: function() {
        return{
            series: {
                lines: {
                    show: false
                },
                points: {
                    show: true
                }
            },
            xaxis: {
                axisLabel: "Global number of requests per second",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            yaxis: {
                axisLabel: "Median Latency in ms",
                axisLabelUseCanvas: true,
                axisLabelFontSizePixels: 12,
                axisLabelFontFamily: 'Verdana, Arial',
                axisLabelPadding: 20,
            },
            legend: { noColumns: 2,show: true, container: '#legendLatencyVsRequest' },
            selection: {
                mode: 'xy'
            },
            grid: {
                hoverable: true // IMPORTANT! this is needed for tooltip to work
            },
            tooltip: true,
            tooltipOpts: {
                content: "%s : Median Latency time at %x req/s was %y ms"
            },
            colors: ["#9ACD32", "#FF6347"]
        };
    },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesLatencyVsRequest"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotLatenciesVsRequest"), dataset, options);
        // setup overview
        $.plot($("#overviewLatenciesVsRequest"), dataset, prepareOverviewOptions(options));
    }
};

// Latencies vs Request
function refreshLatenciesVsRequest() {
        var infos = latenciesVsRequestInfos;
        prepareSeries(infos.data);
        if(isGraph($("#flotLatenciesVsRequest"))){
            infos.createGraph();
        }else{
            var choiceContainer = $("#choicesLatencyVsRequest");
            createLegend(choiceContainer, infos);
            infos.createGraph();
            setGraphZoomable("#flotLatenciesVsRequest", "#overviewLatenciesVsRequest");
            $('#footerLatenciesVsRequest .legendColorBox > div').each(function(i){
                $(this).clone().prependTo(choiceContainer.find("li").eq(i));
            });
        }
};

var hitsPerSecondInfos = {
        data: {"result": {"minY": 5.333333333333333, "minX": 1.77688284E12, "maxY": 5.333333333333333, "series": [{"data": [[1.77688284E12, 5.333333333333333]], "isOverall": false, "label": "hitsPerSecond", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.77688284E12, "title": "Hits Per Second"}},
        getOptions: function() {
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of hits / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendHitsPerSecond"
                },
                selection: {
                    mode : 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y.2 hits/sec"
                }
            };
        },
        createGraph: function createGraph() {
            var data = this.data;
            var dataset = prepareData(data.result.series, $("#choicesHitsPerSecond"));
            var options = this.getOptions();
            prepareOptions(options, data);
            $.plot($("#flotHitsPerSecond"), dataset, options);
            // setup overview
            $.plot($("#overviewHitsPerSecond"), dataset, prepareOverviewOptions(options));
        }
};

// Hits per second
function refreshHitsPerSecond(fixTimestamps) {
    var infos = hitsPerSecondInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if (isGraph($("#flotHitsPerSecond"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesHitsPerSecond");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotHitsPerSecond", "#overviewHitsPerSecond");
        $('#footerHitsPerSecond .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
}

var codesPerSecondInfos = {
        data: {"result": {"minY": 5.333333333333333, "minX": 1.77688284E12, "maxY": 5.333333333333333, "series": [{"data": [[1.77688284E12, 5.333333333333333]], "isOverall": false, "label": "200", "isController": false}], "supportsControllersDiscrimination": false, "granularity": 60000, "maxX": 1.77688284E12, "title": "Codes Per Second"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of responses / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendCodesPerSecond"
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "Number of Response Codes %s at %x was %y.2 responses / sec"
                }
            };
        },
    createGraph: function() {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesCodesPerSecond"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotCodesPerSecond"), dataset, options);
        // setup overview
        $.plot($("#overviewCodesPerSecond"), dataset, prepareOverviewOptions(options));
    }
};

// Codes per second
function refreshCodesPerSecond(fixTimestamps) {
    var infos = codesPerSecondInfos;
    prepareSeries(infos.data);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotCodesPerSecond"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesCodesPerSecond");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotCodesPerSecond", "#overviewCodesPerSecond");
        $('#footerCodesPerSecond .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var transactionsPerSecondInfos = {
        data: {"result": {"minY": 5.333333333333333, "minX": 1.77688284E12, "maxY": 5.333333333333333, "series": [{"data": [[1.77688284E12, 5.333333333333333]], "isOverall": false, "label": "GET /api/demo/counter/race-condition-success", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.77688284E12, "title": "Transactions Per Second"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of transactions / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendTransactionsPerSecond"
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y transactions / sec"
                }
            };
        },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesTransactionsPerSecond"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotTransactionsPerSecond"), dataset, options);
        // setup overview
        $.plot($("#overviewTransactionsPerSecond"), dataset, prepareOverviewOptions(options));
    }
};

// Transactions per second
function refreshTransactionsPerSecond(fixTimestamps) {
    var infos = transactionsPerSecondInfos;
    prepareSeries(infos.data);
    if(infos.data.result.series.length == 0) {
        setEmptyGraph("#bodyTransactionsPerSecond");
        return;
    }
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotTransactionsPerSecond"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesTransactionsPerSecond");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotTransactionsPerSecond", "#overviewTransactionsPerSecond");
        $('#footerTransactionsPerSecond .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

var totalTPSInfos = {
        data: {"result": {"minY": 5.333333333333333, "minX": 1.77688284E12, "maxY": 5.333333333333333, "series": [{"data": [[1.77688284E12, 5.333333333333333]], "isOverall": false, "label": "Transaction-success", "isController": false}, {"data": [], "isOverall": false, "label": "Transaction-failure", "isController": false}], "supportsControllersDiscrimination": true, "granularity": 60000, "maxX": 1.77688284E12, "title": "Total Transactions Per Second"}},
        getOptions: function(){
            return {
                series: {
                    lines: {
                        show: true
                    },
                    points: {
                        show: true
                    }
                },
                xaxis: {
                    mode: "time",
                    timeformat: getTimeFormat(this.data.result.granularity),
                    axisLabel: getElapsedTimeLabel(this.data.result.granularity),
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20,
                },
                yaxis: {
                    axisLabel: "Number of transactions / sec",
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial',
                    axisLabelPadding: 20
                },
                legend: {
                    noColumns: 2,
                    show: true,
                    container: "#legendTotalTPS"
                },
                selection: {
                    mode: 'xy'
                },
                grid: {
                    hoverable: true // IMPORTANT! this is needed for tooltip to
                                    // work
                },
                tooltip: true,
                tooltipOpts: {
                    content: "%s at %x was %y transactions / sec"
                },
                colors: ["#9ACD32", "#FF6347"]
            };
        },
    createGraph: function () {
        var data = this.data;
        var dataset = prepareData(data.result.series, $("#choicesTotalTPS"));
        var options = this.getOptions();
        prepareOptions(options, data);
        $.plot($("#flotTotalTPS"), dataset, options);
        // setup overview
        $.plot($("#overviewTotalTPS"), dataset, prepareOverviewOptions(options));
    }
};

// Total Transactions per second
function refreshTotalTPS(fixTimestamps) {
    var infos = totalTPSInfos;
    // We want to ignore seriesFilter
    prepareSeries(infos.data, false, true);
    if(fixTimestamps) {
        fixTimeStamps(infos.data.result.series, 10800000);
    }
    if(isGraph($("#flotTotalTPS"))){
        infos.createGraph();
    }else{
        var choiceContainer = $("#choicesTotalTPS");
        createLegend(choiceContainer, infos);
        infos.createGraph();
        setGraphZoomable("#flotTotalTPS", "#overviewTotalTPS");
        $('#footerTotalTPS .legendColorBox > div').each(function(i){
            $(this).clone().prependTo(choiceContainer.find("li").eq(i));
        });
    }
};

// Collapse the graph matching the specified DOM element depending the collapsed
// status
function collapse(elem, collapsed){
    if(collapsed){
        $(elem).parent().find(".fa-chevron-up").removeClass("fa-chevron-up").addClass("fa-chevron-down");
    } else {
        $(elem).parent().find(".fa-chevron-down").removeClass("fa-chevron-down").addClass("fa-chevron-up");
        if (elem.id == "bodyBytesThroughputOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshBytesThroughputOverTime(true);
            }
            document.location.href="#bytesThroughputOverTime";
        } else if (elem.id == "bodyLatenciesOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshLatenciesOverTime(true);
            }
            document.location.href="#latenciesOverTime";
        } else if (elem.id == "bodyCustomGraph") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshCustomGraph(true);
            }
            document.location.href="#responseCustomGraph";
        } else if (elem.id == "bodyConnectTimeOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshConnectTimeOverTime(true);
            }
            document.location.href="#connectTimeOverTime";
        } else if (elem.id == "bodyResponseTimePercentilesOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshResponseTimePercentilesOverTime(true);
            }
            document.location.href="#responseTimePercentilesOverTime";
        } else if (elem.id == "bodyResponseTimeDistribution") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshResponseTimeDistribution();
            }
            document.location.href="#responseTimeDistribution" ;
        } else if (elem.id == "bodySyntheticResponseTimeDistribution") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshSyntheticResponseTimeDistribution();
            }
            document.location.href="#syntheticResponseTimeDistribution" ;
        } else if (elem.id == "bodyActiveThreadsOverTime") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshActiveThreadsOverTime(true);
            }
            document.location.href="#activeThreadsOverTime";
        } else if (elem.id == "bodyTimeVsThreads") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshTimeVsThreads();
            }
            document.location.href="#timeVsThreads" ;
        } else if (elem.id == "bodyCodesPerSecond") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshCodesPerSecond(true);
            }
            document.location.href="#codesPerSecond";
        } else if (elem.id == "bodyTransactionsPerSecond") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshTransactionsPerSecond(true);
            }
            document.location.href="#transactionsPerSecond";
        } else if (elem.id == "bodyTotalTPS") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshTotalTPS(true);
            }
            document.location.href="#totalTPS";
        } else if (elem.id == "bodyResponseTimeVsRequest") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshResponseTimeVsRequest();
            }
            document.location.href="#responseTimeVsRequest";
        } else if (elem.id == "bodyLatenciesVsRequest") {
            if (isGraph($(elem).find('.flot-chart-content')) == false) {
                refreshLatenciesVsRequest();
            }
            document.location.href="#latencyVsRequest";
        }
    }
}

/*
 * Activates or deactivates all series of the specified graph (represented by id parameter)
 * depending on checked argument.
 */
function toggleAll(id, checked){
    var placeholder = document.getElementById(id);

    var cases = $(placeholder).find(':checkbox');
    cases.prop('checked', checked);
    $(cases).parent().children().children().toggleClass("legend-disabled", !checked);

    var choiceContainer;
    if ( id == "choicesBytesThroughputOverTime"){
        choiceContainer = $("#choicesBytesThroughputOverTime");
        refreshBytesThroughputOverTime(false);
    } else if(id == "choicesResponseTimesOverTime"){
        choiceContainer = $("#choicesResponseTimesOverTime");
        refreshResponseTimeOverTime(false);
    }else if(id == "choicesResponseCustomGraph"){
        choiceContainer = $("#choicesResponseCustomGraph");
        refreshCustomGraph(false);
    } else if ( id == "choicesLatenciesOverTime"){
        choiceContainer = $("#choicesLatenciesOverTime");
        refreshLatenciesOverTime(false);
    } else if ( id == "choicesConnectTimeOverTime"){
        choiceContainer = $("#choicesConnectTimeOverTime");
        refreshConnectTimeOverTime(false);
    } else if ( id == "choicesResponseTimePercentilesOverTime"){
        choiceContainer = $("#choicesResponseTimePercentilesOverTime");
        refreshResponseTimePercentilesOverTime(false);
    } else if ( id == "choicesResponseTimePercentiles"){
        choiceContainer = $("#choicesResponseTimePercentiles");
        refreshResponseTimePercentiles();
    } else if(id == "choicesActiveThreadsOverTime"){
        choiceContainer = $("#choicesActiveThreadsOverTime");
        refreshActiveThreadsOverTime(false);
    } else if ( id == "choicesTimeVsThreads"){
        choiceContainer = $("#choicesTimeVsThreads");
        refreshTimeVsThreads();
    } else if ( id == "choicesSyntheticResponseTimeDistribution"){
        choiceContainer = $("#choicesSyntheticResponseTimeDistribution");
        refreshSyntheticResponseTimeDistribution();
    } else if ( id == "choicesResponseTimeDistribution"){
        choiceContainer = $("#choicesResponseTimeDistribution");
        refreshResponseTimeDistribution();
    } else if ( id == "choicesHitsPerSecond"){
        choiceContainer = $("#choicesHitsPerSecond");
        refreshHitsPerSecond(false);
    } else if(id == "choicesCodesPerSecond"){
        choiceContainer = $("#choicesCodesPerSecond");
        refreshCodesPerSecond(false);
    } else if ( id == "choicesTransactionsPerSecond"){
        choiceContainer = $("#choicesTransactionsPerSecond");
        refreshTransactionsPerSecond(false);
    } else if ( id == "choicesTotalTPS"){
        choiceContainer = $("#choicesTotalTPS");
        refreshTotalTPS(false);
    } else if ( id == "choicesResponseTimeVsRequest"){
        choiceContainer = $("#choicesResponseTimeVsRequest");
        refreshResponseTimeVsRequest();
    } else if ( id == "choicesLatencyVsRequest"){
        choiceContainer = $("#choicesLatencyVsRequest");
        refreshLatenciesVsRequest();
    }
    var color = checked ? "black" : "#818181";
    if(choiceContainer != null) {
        choiceContainer.find("label").each(function(){
            this.style.color = color;
        });
    }
}

