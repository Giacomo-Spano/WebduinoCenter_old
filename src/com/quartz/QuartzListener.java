package com.quartz;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.SimpleScheduleBuilder.*;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.server.webduino.core.Core;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.logging.Logger;

import javax.servlet.ServletContext;


public class QuartzListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(QuartzListener.class.getName());
    public static final String CoreClass = "core";

    Scheduler scheduler = null;

    static Core core;// = new Core();

    @Override
    public void contextInitialized(ServletContextEvent servletContext) {

        core = new Core();
        core.init();

        ServletContext cntxt = servletContext.getServletContext();
        cntxt.setAttribute(CoreClass, core);


        System.out.println("Context Initialized");

        try {
            scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();

            //pass the servlet context to the job
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("servletContext", servletContext.getServletContext());
            // define the job and tie it to our job's class
            JobDetail sensorJob = newJob(ShieldsQuartzJob.class).withIdentity(
                    "CronSensorQuartzJob", "Group")
                    .usingJobData(jobDataMap)
                    .build();
            // Trigger the job to run now, and then every 40 seconds
            Trigger sensorTrigger = newTrigger()
                    .withIdentity("SensorTriggerName", "Group")
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60)
                            .repeatForever())
                    .build();
            // Setup the Job and Trigger with Scheduler & schedule jobs
            scheduler.scheduleJob(sensorJob, sensorTrigger);

            // Setup the Job class and the Job group
            JobDetail programJob = newJob(ProgramQuartzJob.class).withIdentity(
                    "CronProgramQuartzJob", "Group")
                    .usingJobData(jobDataMap)
                    .build();
            // Trigger the job to run now, and then every 40 seconds
            Trigger trigger = newTrigger()
                    .withIdentity("ProgramTriggerName", "Group")
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(30)
                            .repeatForever())
                    .build();
            // Setup the Job and Trigger with Scheduler & schedule jobs
            //scheduler.scheduleJob(programJob, trigger);

            //Build a trigger for a specific moment in time, with no repeats:
            /*SimpleTrigger trigger = (SimpleTrigger) newTrigger()
                    .withIdentity("trigger1", "group1")
                    .startAt(myStartTime) // some Date
                    .forJob("job1", "group1") // identify job with name, group strings
                    .build();*/


            //scheduler2 = new StdSchedulerFactory().getScheduler();
            //scheduler2.start();
            // Setup the Job class and the Job group
            /*JobDetail recoveryJob = newJob(NextProgramQuartzJob.class).withIdentity(
                    "CronRecoveryQuartzJob", "Group2").build();*/
            // Trigger the job to run now, and then every 40 seconds
            /*Trigger recoveryTrigger = newTrigger()
                    .withIdentity("RecoveryTriggerName", "Group2")
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(3000)
                            .repeatForever())
                    .build();*/
            // Setup the Job and Trigger with Scheduler & schedule jobs
            //scheduler2.scheduleJob(recoveryJob, recoveryTrigger);



        }
        catch (SchedulerException e) {
            LOGGER.info("QuartzListener exception" + e.getStackTrace());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContext) {
        System.out.println("Context Destroyed");
        try
        {
            scheduler.shutdown();
        }
        catch (SchedulerException e)
        {
            LOGGER.info("execute" + e.getStackTrace());
            e.printStackTrace();
        }
    }
}
