package com.paascloud.provider.pmc.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

public class MyElasticSmipleJob  implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        switch (shardingContext.getShardingItem()) {
            case 0:
                System.out.println("========================0==========================");
                break;
            case 1:
                System.out.println("========================1==========================");
                break;
            case 2:
                System.out.println("========================2==========================");
                break;
            // case n: ...
        }
    }
}
