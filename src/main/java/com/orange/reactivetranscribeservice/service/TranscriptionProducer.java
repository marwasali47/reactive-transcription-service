package com.orange.reactivetranscribeservice.service;


import com.orange.reactivetranscribeservice.dtos.TranscribeRequest;
import com.orange.reactivetranscribeservice.dtos.TranscribeResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;

@Component
public class TranscriptionProducer {


    public Flux<TranscribeResponse> transcribe(TranscribeRequest request){
       /* return Flux.fromIterable(
                Arrays.asList(
                        new TranscribeResponse("shello " + request.getMessage()
                )
        ));*/

        throw new RuntimeException("errorrrrrrrrrrrrrrrrrr");

       /* return Flux.fromStream(Stream.generate(new Supplier<TranscribeResponse>() {
            @Override
            public TranscribeResponse get() {
                return new TranscribeResponse("shello " + request.getMessage());
            }
        }));*/
    }
}
