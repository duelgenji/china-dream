! function(win) {

    /**
     * 批量获取queryString中的变量值
     * @param  {[String]} queryKey [待查询的变量名：支持逗号分隔进行批量查询]
     * @return {[String|Array]}          [当只查询单个的时候返回的是字符串形式的变量值,当查询多个的情况下返回字符串数组]
     * Note:如何变量名称不存的时候,该变量在返回的数组或字符串中位置的值为null
     */
    function factory() {
        function queryString(queryKey, uri) {
            if (!queryKey) return null;
            var queryString = uri || ((this.location || win.location).search.toLowerCase()),
                arr = queryKey.split(','),
                sindex, eindex;
            for (var i = arr.length, s; s = arr[--i];) {
                sindex = queryString.indexOf(s.toLowerCase());
                if (sindex == -1) {
                    arr[i] = null;
                    continue;
                }
                sindex += s.length + 1;
                eindex = queryString.indexOf("&", sindex);
                if (eindex == -1) {
                    arr[i] = queryString.substring(sindex);
                    queryString = queryString.substring(0, sindex);
                    continue;
                }
                arr[i] = queryString.substring(sindex, eindex);
                queryString = queryString.substring(0, sindex) + queryString.substring(eindex + 1);
            }
            return arr.length == 1 ? arr[0] : arr;
        };
        
        return {
            getSearch:queryString
        };
    }


    if (typeof define == "function") {
        define("pure-url", [],function() {
            return factory();
        });
    }

}(this);