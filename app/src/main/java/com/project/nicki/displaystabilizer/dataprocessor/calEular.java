
package com.project.nicki.displaystabilizer.dataprocessor;

import com.project.nicki.displaystabilizer.dataprocessor.utils.Filters.filterSensorData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nickisverygood on 3/6/2016.
 */
public class calEular {
    //init filters
    filterSensorData filtercalEular_ACCE = new filterSensorData.Builder().moveingavg_sample(30).highpass_alpha(0.5f).lowpass_alpha(0.6f).kalmanfilter_boo(true).build();
    filterSensorData filtercalEular_VELO = new filterSensorData.Builder().build();
    filterSensorData filtercalEular_POSI = new filterSensorData.Builder().build();


    static final float NS2S = 1.0f / 1000000000.0f;
    float[] last_values;
    float[] velocity = new float[]{0, 0, 0};
    float[] position = new float[]{0, 0, 0};
    List<SensorCollect.sensordata> locationList = new ArrayList<>();
    long last_timestamp = 0;

    public float[] calcList(List<SensorCollect.sensordata> msensordataList){
        for (SensorCollect.sensordata msensordata:msensordataList){
            calc(msensordata);
        }
        return position;
    }
    public void calc(SensorCollect.sensordata msensordata_world) {
        //Filter
        msensordata_world.setData(filtercalEular_ACCE.filter(msensordata_world.getData()));
        velocity = filtercalEular_VELO.filter(velocity);
        position = filtercalEular_POSI.filter(position);

        if (last_values != null) {
            float dt = (msensordata_world.getTime() - last_timestamp) * NS2S;

            for (int index = 0; index < 3; ++index) {
                velocity[index] += (msensordata_world.getData()[index] + last_values[index]) / 2 * dt;
                position[index] += velocity[index] * dt;
            }
        } else {
            last_values = new float[3];
            velocity = new float[3];
            position = new float[3];
            velocity[0] = velocity[1] = velocity[2] = 0f;
            position[0] = position[1] = position[2] = 0f;
        }
        System.arraycopy(msensordata_world.getData(), 0, last_values, 0, 3);
        last_timestamp = msensordata_world.getTime();
        locationList.add(new SensorCollect.sensordata(msensordata_world.getTime(),position, SensorCollect.sensordata.TYPE.LOCA));
        //LogCSV(String.valueOf(msensordata.getData()[0]),String .valueOf(msensordata.getData()[1]),String.valueOf(velocity[0]),String.valueOf(velocity[1]),String.valueOf(position[0]),String.valueOf(position[1]));
    }

}

