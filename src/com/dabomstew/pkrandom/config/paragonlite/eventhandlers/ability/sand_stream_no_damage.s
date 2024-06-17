    PUSH {R3, R4, LR}
    ADD  SP, #-4
    MOV  R4, #4 ; sandstorm
    STR  R4, [SP, #0]
    BL   Battle::CommonWeatherGuard
    ADD  SP, #4
    POP  {R3, R4, PC}