    push    {r3-r7, lr}
    mov     r4, r0
    
    mov     r5, #FALSE
    
    ldr     r7, =ServerFlow.setTarget
    ldr     r6, [r4, r7]
    
    ldr     r1, =ServerFlow.turnCheckSeq
    ldrb    r1, [r4, r1]
    cmp     r1, #0
    bne     ProcessTurnCheckSeq
    
InitializeTurnCheckSeq:
    mov     r1, r6
    bl      Battle::ServerControl_SortBySpeed
    
    mov     r0, #1
    ldr     r1, =ServerFlow.turnCheckSeq
    strb    r0, [r4, r1]
    
    mov     r0, r4
    bl      Battle::ServerControl_TurnCheck_CommSupport


ProcessTurnCheckSeq:
    ldr     r0, =ServerFlow.turnCheckSeq
    ldrb    r1, [r4, r0]
    cmp     r1, #6
    bls     RunSwitch
    b       Return


RunSwitch:
    #SWITCH r2 r1
    #CASE Return
    #CASE WeatherStep
    #CASE TerrainStep
    #CASE TurnBeginStep
    #CASE ConditionStep
    #CASE SideFieldStep
    #CASE TurnEndStep
    #CASE TurnDoneStep


WeatherStep:
    ; increment turn check seq
    add     r1, #1
    strb    r1, [r4, r0]
    
    mov     r5, #FALSE
    
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    mov     r1, #SCMD_MsgWinHide
    mov     r2, #0
    bl      Battle::ServerDisplay_AddCommon
    
    mov     r0, r4
    mov     r1, r6
    bl      Battle::ServerControl_TurnCheck_Weather
    cmp     r0, #FALSE
    beq     WeatherStep_CheckMatchup


ReturnTrue:
    mov     r5, #TRUE
    b       Return


WeatherStep_CheckMatchup:
    mov     r0, r4
    bl      Battle::ServerControl_CheckMatchup
    cmp     r0, #FALSE
    beq     TerrainStep
    
    b       Return
    
    
; NEW
TerrainStep:
    ; increment turn check seq
    ldr     r0, =ServerFlow.turnCheckSeq
    ldrb    r1, [r4, r0]
    add     r1, #1
    strb    r1, [r4, r0]
    
    mov     r5, #FALSE
    
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    mov     r1, #SCMD_MsgWinHide
    mov     r2, #0
    bl      Battle::ServerDisplay_AddCommon
    
    mov     r0, r4
    mov     r1, r6
    bl      Battle::ServerControl_TurnCheckTerrain
    cmp     r0, #FALSE
    beq     TerrainStep_CheckMatchup
    
    b       ReturnTrue
    

TerrainStep_CheckMatchup:
    mov     r0, r4
    bl      Battle::ServerControl_CheckMatchup
    cmp     r0, #FALSE
    beq     TurnBeginStep
    
    b       Return
; ~NEW


TurnBeginStep:
    ; increment turn check seq
    ldr     r0, =ServerFlow.turnCheckSeq
    ldrb    r1, [r4, r0]
    add     r1, #1
    strb    r1, [r4, r0]
    
    mov     r0, r4
    mov     r1, r6
    mov     r2, #EVENT_OnTurnCheckBegin
    bl      Battle::ServerControl_TurnCheck_Sub
    cmp     r0, #FALSE
    beq     TurnBeginStep_CheckMatchup
    
    b       ReturnTrue


TurnBeginStep_CheckMatchup:
    mov     r0, r4
    bl      Battle::ServerControl_CheckMatchup
    cmp     r0, #FALSE
    beq     ConditionStep
    

ReturnFalse:
    mov     r5, #FALSE
    b       Return


ConditionStep:
    ; increment turn check seq
    ldr     r0, =ServerFlow.turnCheckSeq
    ldrb    r1, [r4, r0]
    add     r1, #1
    strb    r1, [r4, r0]
    
    mov     r0, r4
    mov     r1, r6
    bl      Battle::ServerControl_TurnCheck_Condition
    cmp     r0, #FALSE
    beq     ConditionStep_CheckMatchup
    
    b       ReturnTrue


ConditionStep_CheckMatchup:
    mov     r0, r4
    bl      Battle::ServerControl_CheckMatchup
    cmp     r0, #FALSE
    beq     SideFieldStep
    
    b       ReturnFalse


SideFieldStep:
    ; increment turn check seq
    ldr     r0, =ServerFlow.turnCheckSeq
    ldrb    r1, [r4, r0]
    add     r1, #1
    strb    r1, [r4, r0]
    
    mov     r0, r4
    bl      Battle::ServerControl_TurnCheck_Side
    mov     r0, r4
    bl      Battle::ServerControl_TurnCheck_Field


TurnEndStep:
    ; increment turn check seq
    ldr     r0, =ServerFlow.turnCheckSeq
    ldrb    r1, [r4, r0]
    add     r1, #1
    strb    r1, [r4, r0]
    
    mov     r0, r4
    mov     r1, r6
    mov     r2, #EVENT_OnTurnCheckEnd
    bl      Battle::ServerControl_TurnCheck_Sub
    cmp     r0, #FALSE
    beq     TurnEndStep_CheckMatchup
    
    b       ReturnTrue


TurnEndStep_CheckMatchup:
    mov     r0, r4
    bl      Battle::ServerControl_CheckMatchup
    cmp     r0, #FALSE
    beq     TurnDoneStep
    
    b       ReturnFalse


TurnDoneStep:
    mov     r0, r4
    mov     r1, r6
    mov     r2, #EVENT_OnTurnCheckDone
    bl      Battle::ServerControl_TurnCheck_Sub
    
    ; increment turn check seq
    ldr     r0, =ServerFlow.turnCheckSeq
    ldrb    r1, [r4, r0]
    add     r1, #1
    strb    r1, [r4, r0]
    
    mov     r0, r6
    bl      BattleServer::PokeSet_SeekStart
    
    mov     r0, r6
    bl      BattleServer::PokeSet_SeekNext
    mov     r5, r0
    beq     CheckResetShift
    
    mov     r7, #46


ClearPokeFlagLoop:
    mov     r0, r5
    bl      Battle::Poke_TurnCheck
    
    mov     r0, r5
    bl      Battle::ComboMove_ClearParam
    
    mov     r0, r5
    bl      Battle::GetPokeId
    mov     r2, r0
    ldr     r0, [r4, #ServerFlow.serverCommandQueue]
    mov     r1, r7
    bl      Battle::ServerDisplay_AddCommon
    
    mov     r0, r6
    bl      BattleServer::PokeSet_SeekNext
    mov     r5, r0
    bne     ClearPokeFlagLoop


CheckResetShift:
    mov     r0, r4
    bl      Battle::ServerControl_CheckResetShift
    
    bl      Battle::Event_RemoveIsolatedItems
    
    ldr     r0, =9999
    ldr     r1, [r4, #ServerFlow.turnCount]
    cmp     r1, r0
    bcs     Finish
    
    add     r0, r1, #1
    str     r0, [r4, #ServerFlow.turnCount]


Finish:
    mov     r5, #0
    
    ldr     r1, =ServerFlow.turnCheckSeq
    strb    r5, [r4, r1]
    
    ldr     r1, =ServerFlow.simulationCounter
    str     r5, [r4, r1]

Return:
    mov     r0, r5
    pop     {r3-r7, pc}