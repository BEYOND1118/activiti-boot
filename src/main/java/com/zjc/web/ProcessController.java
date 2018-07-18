package com.zjc.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import com.zjc.vo.ProcessVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ProcessController {
    
    @Autowired
    private RuntimeService runtimeService; //跟运行时相关的运行时服务
    
    @Autowired
    private IdentityService identityService; //跟用户实体相关的用户实体服务
    
    @Autowired
    private TaskService taskService; //跟任务相关的任务服务

    /**
     * 接受前端"我要请假"信息的提交请求
     * @param days 请假天数
     * @param reason 请假原因
     * @param session session域
     * @return
     */
    @RequestMapping("/vac/start")
    public String startVacProcess(Integer days, String reason, HttpSession session) {
        
    	//从session域中获取标签为"user"所对应的值
    	String userId = (String)session.getAttribute("user");
        
    	//通过用户实体服务为当前执行该代码的线程设置一个userId值
    	//目的是：当该线程启动流程的时候，就把该值作为启动流程的值，方面以后查看流程是被谁启动的
        identityService.setAuthenticatedUserId(userId);
        
        //通过流程模板vacation.bpmn的key值来启动流程实例（流程对象）
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("vacationProcess");
        
        //通过流程对象获取任务对象task
        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        
        //使用map集合来封装请假申请的参数
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("days", days);
        vars.put("reason", reason);
        
        //声明当前任务由谁来执行，这样做的目的是：
        //当前OA系统用户组可以有很多用户，每个用户只能查看自己的请假记录，不能查看别人的记录
        taskService.claim(task.getId(), userId);
        
        //完成当前任务（即当前请假流程），接下来流程将会到达经理审批或者总监审批
        taskService.complete(task.getId(), vars);
        
        //返回到welcome.jsp页面
        return "welcome";
    }
    
    
    /**
     * 接收前端"请假记录"的查询请求
     * @param session session域
     * @param model 前端模型
     * @return
     */
    @RequestMapping("/vac/list")
    public String listVac(HttpSession session, Model model) {
    	
    	//从session域中获取标签为"user"所对应的值
        String userId = (String)session.getAttribute("user");
        
        //查询流程实例
        List<ProcessInstance> pis = runtimeService.createProcessInstanceQuery().startedBy(userId).list();
        
        //使用list集合来封装返回到前端的参数
        List<ProcessVO> result = new ArrayList<ProcessVO>();
        
        //遍历流程实例集合
        for(ProcessInstance pi : pis) {
        	
        	//通过流程实例ID获取标签为"days"的值
            Integer days = (Integer)runtimeService.getVariable(pi.getId(), "days");
            //通过流程实例ID获取标签为"reason"的值
            String reason = (String)runtimeService.getVariable(pi.getId(), "reason");
            
            //控制台打印获取到的值
            System.out.println(reason + "---" + days);
            
            //实例化ProcessVO对象v，用来封装遍历到的所有值
            ProcessVO v = new ProcessVO();
            v.setDays(days);
            v.setReason(reason);
            v.setDate(formatDate(pi.getStartTime()));
            
            //将对象v添加到result
            result.add(v);
        }
        
        //将result对象添加到前端model模型，并设置标签"pis"
        model.addAttribute("pis", result);
        
        //返回到vac下面的list.jsp页面
        return "vac/list";
    }
    
    
    /**
     * 时间格式化工具
     * @param d 时间
     * @return
     */
    private static String formatDate(Date d) {
        try {
        	//定义时间格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            //把形参d接收到的时间按照定义的时间格式进行格式化，然后返回给调用者
            return sdf.format(d);
        } catch (Exception e) {
            return "";
        }
    }
}
