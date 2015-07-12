/*
Powered by wgduan
Example:
<SCRIPT LANGUAGE="JavaScript" src="Calendar.js"></SCRIPT>
<input id="date1" type=text ondblclick="c__showCalendar('date1')" value="2104-02-01" title="双击显示日历" onfocus=this.select()>
*/
//
function FreeCalendar(sDate) {
    var c__months = new Array("一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月");
    var c__lastDaysOfMonth = new Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
    var c__weekDays = new Array("日", "一", "二", "三", "四", "五", "六");

    var c__currentDate = null;
    var c__date = null;
    var c__year = null;
    var c__month = null;
    var c__day = null;
    var c__weekDay = null;

    var c__today = new Date();

    var c__sundayColor = "red"; //星期日颜色
    var c__saturdayColor = "green"; //星期六颜色
    var c__normalDayColor = "black"; //普通日期颜色
    var c__calendarTitleBackgroundColor = "#e0e0e0"; //星期标题栏背景色
    var c__currentDayColor = "darkred"; //当前日期颜色
    var c__todayColor = "black"; //今天颜色
    var c__disableDayColor = "lightgray"; //不可选择日期的颜色


    var c__beginYear = 1989; //最小年份
    var c__endYear = c__today.getFullYear() + 50; //最大年份
    var c__beginDate = new Date(c__beginYear + "/01/01");
    var c__endDate = new Date(c__endYear + "/12/31");

    //日期
    this.getDate = function () {
        return c__dateToString(c__date);
    }
    this.setDate = function (sDate) {
        c__date = c__stringSafeToDate(sDate);
        c__date = (c__date < c__beginDate) ? c__beginDate : c__date;
        c__date = (c__date > c__endDate) ? c__endDate : c__date;

        c__year = c__date.getFullYear();
        c__month = c__date.getMonth();
        c__day = c__date.getDate();
        c__weekDay = c__date.getDay();
    }
    //原始输入日期
    this.getCurrentDate = function (sDate) {
        return c__dateToString(c__currentDate);
    }
    this.setCurrentDate = function (sDate) {
        c__currentDate = c__stringSafeToDate(sDate);
    }
    //年
    this.getYear = function () {
        return c__year;
    }
    this.setYear = function (year) {
        c__year = year;
    }
    //月
    this.getMonth = function () {
        return c__month;
    }
    this.setMonth = function (month) {
        c__month = month;
    }
    //日
    this.getDay = function () {
        return c__day;
    }
    this.setDay = function (day) {
        c__day = day;
    }
    //星期
    this.getWeekDay = function () {
        return c__weekDay;
    }
    this.setWeekDay = function (weekDay) {
        c__weekDay = weekDay;
    }
    //是否闰年
    this.isLeapYear = function () {
        return ((0 == this.getYear() % 4) && (0 != (this.getYear() % 100))) || (0 == this.getYear() % 400);
    }
    //获取当月最后一天的日期
    this.getLastDayOfMonth = function () {
        if (this.getMonth() == 1) {
            return (this.isLeapYear()) ? 29 : 28;
        } else {
            return c__lastDaysOfMonth[this.getMonth()];
        }
    }

    //绘制日历框架
    this.renderCalendar = function () {
        var s = "";

        s += "<div id=\"c__calendarDiv\" style=\"width:100%;display:none;z-index:100;backgroundColor:#ffffff\">";
        s += "<table border=\"0\" bgcolor=\"#ffffff\" width=\"100%\" align=\"center\" style=\"border-collapse:collapse;font-size:12px;font-family:宋体;cursor:default;\" oncontextmenu=\"return false;\" onselectstart=\"return false;\">";
        //日历头部
        s += "<tr>";
        s += "<td>";
        s += this.renderCalendarHead(this.getYear(), this.getMonth());
        s += "</td>";
        s += "</tr>";
        //日历日期部分
        s += "<tr>";
        s += "<td style=\"border-top:1px solid silver;\">";
        s += this.renderDateGrid();
        s += "</td>";
        s += "</tr>";
        //日历底部
        s += "<tr>";
        s += "<td style=\"border-top:1px solid silver;\">";
        s += this.renderCalendarFoot();
        s += "</td>";
        s += "</tr>";

        s += "</table>";
        s += "</div>";
        document.write(s);
    }



    //绘制日期头部
    this.renderCalendarHead = function (year, month) {
        var s = "";
        s += "<div id='calendarHead'><table border=\"0\" width=\"100%\" style=\"font-size:21px;font-family:宋体;\" cellspacing=\"0\">";
        s += "<tr>";
        //年份下拉框
        s += "<td>";
        s += "<select id=\"c__yearSelector\" onchange=\"parent.c__cal.changeDate(this.value,document.getElementById('c__monthSelector').value)\" style=\"width:100%;font-size:12px\">";
        for (var i = c__beginYear; i <= c__endYear; ++i) {
            var selected = "";
            if (i == year) {
                selected = "selected";
            }
            s += "<option " + selected + " value=\"" + i + "\">" + i + "</option>";
        }
        s += "</select>";
        s += "</td>";

        //月份下拉框
        s += "<td align=\"right\">";
        s += "<select id=\"c__monthSelector\" onchange=\"parent.c__cal.changeDate(document.getElementById('c__yearSelector').value,this.value)\" style=\"width:100%;font-size:12px\">";
        for (var i = 0; i < c__months.length; ++i) {
            var selected = "";
            if (i == month) {
                selected = "selected";
            }
            s += "<option " + selected + " value=\"" + i + "\">" + c__months[i] + "</option>";
        }
        s += "</select>";
        s += "</td>";

        s += "</tr>";
        s += "</table></div>";
        return s;
    }
    //绘制日历日期
    this.renderDateGrid = function () {
        var s = "";
        s += "<table border=\"0\" width=\"100%\" id=\"c__dateGrid\" cellspacing=\"0\" style=\"font-size:12px;font-family:宋体;\">";
        //星期标题
        s += "<tr style=\"background-color:" + c__calendarTitleBackgroundColor + ";\">";
        for (var i = 0; i < c__weekDays.length; ++i) {
            //日期颜色
            var dayColor = "";
            switch (i) {
                case 0:
                    dayColor = c__sundayColor;
                    break;
                case 6:
                    dayColor = c__saturdayColor;
                    break;
                default:
                    dayColor = c__normalDayColor;
                    break;
            }
            s += "<td align=\"center\" valign=\"middle\" height=\"20\" style=\"color:" + dayColor + ";border-bottom:1px solid silver;\"><b>" + c__weekDays[i] + "</b></td>";
        }
        s += "</tr>";

        //日期
        for (var i = 0; i < 6; ++i) {
            s += "<tr>";
            for (var r = 0; r < c__weekDays.length; ++r) {
                s += "<td align=\"center\" valign=\"middle\" height=\"20\"  style=\"color:" + dayColor + ";cursor:pointer;\" onclick=\"parent.c__getDate(this.innerText,this)\" onmouseover=\"parent.c__underline(this)\" onmouseout=\"parent.c__deunderline(this)\"></td>";
            }
            s += "</tr>";
        }
        s += "</table>";
        return s;
    }

    //绘制日期底部
    this.renderCalendarFoot = function () {
        var s = "";
        s += "<table width=\"100%\" bgcolor=\"#ffffff\" style=\"font-size:12px;font-family:宋体;\">";
        s += "<tr>";
        s += "<td nowrap width=\"30\" style=\"color:" + c__todayColor + ";\">今天:</td>";
        s += "<td onclick=\"parent.c__input.value=this.innerText;parent.c__hideCalendar()\" style=\"cursor:pointer;color:" + c__todayColor + ";\"onmouseover=\"this.style.textDecoration='underline';\" onmouseout=\"this.style.textDecoration='';\">";
        s += "<b>" + c__dateToString(c__today) + "</b>";
        s += "</td>";
        //s+="<td onclick=\"parent.c__input.value='';parent.c__hideCalendar()\" style=\"cursor:pointer;color:\"><b>清空</b></td>";
        s += "</tr>";
        s += "</table>";
        s += "";
        return s;
    }

    //显示日期
    this.showDate = function () {
        //限制日期
        var today = new Date(), // 获取今天时间
            limitDate = new Date();
        today.setDate(today.getDate() - 1);
        limitDate.setDate(limitDate.getDate() + 60);

        //填充日期值
        var date = 0;
        //本月1号星期值
        var fistWeekDayOfMonth = new Date(this.getYear() + "/" + (this.getMonth() + 1) + "/1").getDay();
        for (i = 0; i < 6; ++i) {
            for (var r = 0; r < c__weekDays.length; ++r) {
                //日期颜色
                var dayColor = "";

                switch (r) {
                    case 0:
                        dayColor = c__sundayColor;
                        break;
                    case 6:
                        dayColor = c__saturdayColor;
                        break;
                    default:
                        dayColor = c__normalDayColor;
                        break;
                }

                var sDate = "";
                var j = i * c__weekDays.length + r;
                if (j >= fistWeekDayOfMonth && j < fistWeekDayOfMonth + this.getLastDayOfMonth()) {
                    date++;
                    sDate = date;

                    var cDate = c__stringToDate(this.getYear() + "-" + (this.getMonth() + 1) + "-" + sDate);
                    if (Date.parse(cDate) > Date.parse(limitDate) || Date.parse(cDate) < Date.parse(today)) {
                        dayColor = c__disableDayColor;
                        document.getElementById("c__dateGrid").rows[i + 1].cells[r].setAttribute("disabled","disabled");
                    }else{
                        document.getElementById("c__dateGrid").rows[i + 1].cells[r].removeAttribute("disabled");
                    }

                    if (this.getYear() == c__currentDate.getFullYear() && this.getMonth() == c__currentDate.getMonth() && date == c__currentDate.getDate()) {
                        //当天日期样式
                        document.getElementById("c__dateGrid").rows[i + 1].cells[r].style.border = "1px solid " + c__currentDayColor;
                    } else {
                        document.getElementById("c__dateGrid").rows[i + 1].cells[r].style.border = "1px solid white";
                    }
                } else {
                    document.getElementById("c__dateGrid").rows[i + 1].cells[r].style.border = "1px solid white";
                }
                document.getElementById("c__dateGrid").rows[i + 1].cells[r].style.color = dayColor;
                document.getElementById("c__dateGrid").rows[i + 1].cells[r].style.backgroundColor = "white";
                document.getElementById("c__dateGrid").rows[i + 1].cells[r].innerHTML = sDate;
            }
        }

        if (document.getElementById("c__calendarFrame") != null) {
            //add by cschen Safari兼容
            var oldEl = document.getElementById("calendarHead");
            var newEl = oldEl.cloneNode(false);
            newEl.innerHTML = this.renderCalendarHead(c__year, c__month);
            oldEl.parentNode.replaceChild(newEl, oldEl);
            c__calendarFrame.document.body.innerHTML = document.getElementById("c__calendarDiv").innerHTML;
        }
    }
    //改变日历年份或月份
    this.changeDate = function (year, month) {
        var sDate = year + "-" + eval(month + "+1") + "-" + ((this.getDay() > c__lastDaysOfMonth[month]) ? c__lastDaysOfMonth[month] : this.getDay());
        this.setDate(sDate);
        this.showDate();

    }
    //初始化
    this.initialize = function () {
        this.setCurrentDate(sDate);
        this.setDate(sDate);
        this.renderCalendar();
        this.showDate();

    };
    this.initialize();
}

//转换字符串日期
function c__stringToDate(sDate) {
    var regex = new RegExp("\\d{8}");
    if (regex.test(sDate)) {
        sDate = sDate.substr(0, 4) + "/" + sDate.substr(4, 2) + "/" + sDate.substr(6, 2)
    } else {
        sDate = sDate.replace(/-/g, "/");
    }
    return new Date(sDate);
}

//安全转换字符串日期,非法日期转换为今天
function c__stringSafeToDate(sDate) {
    var regex = new RegExp("\\d+-\\d+-\\d+");
    if (!regex.test(sDate)) {
        return new Date();
    } else {
        try {
            var date = c__stringToDate(sDate.match(regex)[0]);
            if (isNaN(date)) {
                return new Date();
            } else {
                return date;
            }
        } catch (err) {
            return new Date();
        }

    }
}

//转换日期到字符串
function c__dateToString(date) {
    var year;
    var month;
    var day;

    year = date.getFullYear();
    month = date.getMonth() + 1;
    day = date.getDate();

    month = "0" + month;
    month = month.substring(month.length - 2, month.length);
    day = "0" + day;
    day = day.substring(day.length - 2, day.length);

    return year + "-" + month + "-" + day;
}

//获取绝对位置
function c__getPosition(obj) {

    var x, y;
    var coordinates = new Object();
    var tmp_obj = obj;
    x = 0;
    y = 0;
    while (tmp_obj != null && tmp_obj.nodeName != "BODY") {
        x += tmp_obj.offsetLeft;
        y += tmp_obj.offsetTop;
        if (tmp_obj.nodeName == "DIV") {
            x -= tmp_obj.scrollLeft;
            y -= tmp_obj.scrollTop;
        }
        tmp_obj = tmp_obj.offsetParent;
    }
    coordinates.x = x;
    coordinates.y = y;
    return coordinates;
}

//获取日期
function c__getDate(day,e) {
    console.log(e);
    console.log(e.getAttribute("disabled"));

    if (day != "" && e.getAttribute("disabled") != "disabled") {
        var date = c__stringToDate(document.getElementById("c__yearSelector").value + "-" + eval(document.getElementById("c__monthSelector").value + "+1") + "-" + day);
        c__input.value = c__dateToString(date);
        c__hideCalendar();
    }
}

function c__underline(e){
    if (e.getAttribute("disabled") != "disabled") {
        e.style.textDecoration = 'underline';
    }
}

function c__deunderline(e){
    e.style.textDecoration = '';
}

//改变日期
function c__changeDate(year, month) {
    var date = year + "-" + eval(month + "+1") + "-" + c__cal.getDay();
    c__cal.setDate(date);
    c__cal.showDate();
}

//检测日期合法
function c__checkDate(objName) {
    var obj = document.getElementById(objName);
    var regex = new RegExp("\\d+-\\d+-\\d+|\\d{8}");
    if (!regex.test(obj.value)) {
        obj.value = "";
    } else {
        var date = c__stringToDate(obj.value.match(regex)[0]);
        if (isNaN(date)) {
            obj.value = "";
        } else {
            obj.value = c__dateToString(date);
        }

    }
}

//显示日历
var __showCalendar = function (event, objName) {
    var obj = document.getElementById(objName);
    c__input = obj;
    c__cal.setCurrentDate(c__input.value);
    c__cal.setDate(c__input.value)
    c__cal.showDate();
    event.cancelBubble = true;
    window.c__calendarFrame.document.body.innerHTML = document.getElementById("c__calendarDiv").innerHTML;
    document.getElementById("c__calendarFrame").style.left = c__getPosition(c__input).x + "px";
    document.getElementById("c__calendarFrame").style.top = c__getPosition(c__input).y + c__input.offsetHeight + "px";
    document.getElementById("c__calendarFrame").style.display = '';
}

//隐藏日历
function c__hideCalendar() {
    document.getElementById("c__calendarFrame").style.display = 'none';
}
var c__input = null;
var c__cal = new FreeCalendar();
document.write('<iframe src="about:blank" frameborder=0 scrolling="no"  MARGINHEIGHT=0 MARGINWIDTH=0 border=0 id="c__calendarFrame" name="c__calendarFrame" onkeydown="c__selectStock()" style="display:none;border:1px solid gray;width:150px;height:195px;overflow:visible;position:absolute;FILTER: progid:DXImageTransform.Microsoft.Shadow(direction=135,color=silver,strength=3);z-index:999; background-color:#ffffff"></iframe>');

if (window.attachEvent) {
    document.body.attachEvent("onclick", function () {
        c__hideCalendar();
    });
} else {
    document.body.addEventListener("click", function () {
        c__hideCalendar();
    }, false); //修改的地方
}