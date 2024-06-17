    PUSH {R3, R4, LR}
    ADD  SP, #-4
    MOV  R4, #3 ; hail
    STR  R4, [SP]
    BL   Battle::CommonWeatherGuard
    ADD  SP, #4
    POP  {R3, R4, PC}