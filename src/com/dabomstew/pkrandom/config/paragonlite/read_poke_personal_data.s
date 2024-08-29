; r0: void* data
; r1: u32   field


#DEFINE MAX_FIELD 47

    cmp     r1, #MAX_FIELD
    bls     Switch
    b       ReturnZero
    
Switch:
    #SWITCH r1
    #CASE Stat_HP
    #CASE Stat_Attack
    #CASE Stat_Defense
    #CASE Stat_Speed
    #CASE Stat_SpAtk
    #CASE Stat_SpDef
    #CASE Type1
    #CASE Type2
    #CASE CatchRate
    #CASE ExpYield
    #CASE EVYield_HP
    #CASE EVYield_Attack
    #CASE EVYield_Defense
    #CASE EVYield_Speed
    #CASE EVYield_SpAtk
    #CASE EVYield_SpDef
    #CASE HasGroundedEntry ; Diglett/Dugtrio
    #CASE Item1
    #CASE Item2
    #CASE Item3
    #CASE GenderRate
    #CASE EggCycles
    #CASE BaseFriendship
    #CASE GrowthRate
    #CASE EggGroup1
    #CASE EggGroup2
    #CASE Ability1
    #CASE Ability2
    #CASE HiddenAbility
    #CASE FleeRate
    #CASE FormStatsOffset
    #CASE FormSpritesOffset
    #CASE FormCount
    #CASE DexColor
    #CASE UnknownFlag
    #CASE IsArceus ; used for forms
    #CASE Stage
    #CASE Height
    #CASE Weight
    #CASE TMMoves1
    #CASE TMMoves2
    #CASE TMMoves3
    #CASE TMMoves4
    #CASE TutorMoves_Starter
    #CASE TutorMoves_Red
    #CASE TutorMoves_Blue
    #CASE TutorMoves_Yellow
    #CASE TutorMoves_Green
    
Stat_HP:
    ldrb    r0, [r0, #0x00]
    bx      lr
    
Stat_Attack:
    ldrb    r0, [r0, #0x01]
    bx      lr
    
Stat_Defense:
    ldrb    r0, [r0, #0x02]
    bx      lr
    
Stat_Speed:
    ldrb    r0, [r0, #0x03]
    bx      lr
    
Stat_SpAtk:
    ldrb    r0, [r0, #0x04]
    bx      lr
    
Stat_SpDef:
    ldrb    r0, [r0, #0x05]
    bx      lr
    
Type1:
    ldrb    r0, [r0, #0x06]
    bx      lr
    
Type2:
    ldrb    r0, [r0, #0x07]
    bx      lr
    
CatchRate:
    ldrb    r0, [r0, #0x08]
    bx      lr
    
ExpYield:
    ldrh    r0, [r0, #0x22]
    bx      lr
    
EVYield_HP:
    ldrh    r0, [r0, #0x0A]
    lsl     r0, #30
    lsr     r0, #30 ; get: 00000000_00000011
    bx      lr
    
EVYield_Attack:
    ldrh    r0, [r0, #0x0A]
    lsl     r0, #28
    lsr     r0, #30 ; get: 00000000_00001100
    bx      lr
    
EVYield_Defense:
    ldrh    r0, [r0, #0x0A]
    lsl     r0, #26
    lsr     r0, #30 ; get: 00000000_00110000
    bx      lr
    
EVYield_Speed:
    ldrh    r0, [r0, #0x0A]
    lsl     r0, #24
    lsr     r0, #30 ; get: 00000000_11000000
    bx      lr
    
EVYield_SpAtk:
    ldrh    r0, [r0, #0x0A]
    lsl     r0, #22
    lsr     r0, #30 ; get: 00000011_00000000
    bx      lr
    
EVYield_SpDef:
    ldrh    r0, [r0, #0x0A]
    lsl     r0, #20
    lsr     r0, #30 ; get: 00001100_00000000
    bx      lr
    
HasGroundedEntry:
    ldrh    r0, [r0, #0x0A]
    lsl     r0, #19
    lsr     r0, #31 ; get: 00010000_00000000
    bx      lr
    
Item1:
    ldrh    r0, [r0, #0x0C]
    bx      lr
    
Item2:
    ldrh    r0, [r0, #0x0E]
    bx      lr
    
Item3:
    ldrh    r0, [r0, #0x10]
    bx      lr
    
GenderRate:
    ldrb    r0, [r0, #0x12]
    bx      lr
    
EggCycles:
    ldrb    r0, [r0, #0x13]
    bx      lr
    
BaseFriendship:
    ldrb    r0, [r0, #0x14]
    bx      lr
    
GrowthRate:
; OLD
;    ldrb    r0, [r0, #0x15]
;    bx      lr

; NEW
    ldrb    r0, [r0, #0x15]
    lsl     r0, #26
    lsr     r0, #26
    bx      lr
    
EggGroup1:
; OLD
;    ldrb    r0, [r0, #0x16]
;    bx      lr
    
; NEW
    ldrb    r0, [r0, #0x16]
    lsl     r0, #26
    lsr     r0, #26
    bx      lr
    
EggGroup2:
; OLD
;    ldrb    r0, [r0, #0x17]
;    bx      lr
    
; NEW
    ldrb    r0, [r0, #0x17]
    lsl     r0, #26
    lsr     r0, #26
    bx      lr
    
Ability1:
; OLD
;    ldrb    r0, [r0, #0x18]
;    bx      lr

; NEW - High bits are top 2 bits from Growth Rate byte
    ldrb    r1, [r0, #0x15]
    lsr     r1, #6
    lsl     r1, #8
    ldrb    r0, [r0, #0x18]
    orr     r0, r1
    bx      lr
    
Ability2:
; OLD
;    ldrb    r0, [r0, #0x19]
;    bx      lr
    
; NEW - High bits are top 2 bits from Egg Group 1 byte
    ldrb    r1, [r0, #0x16]
    lsr     r1, #6
    lsl     r1, #8
    ldrb    r0, [r0, #0x19]
    orr     r0, r1
    bx      lr
    
HiddenAbility:
; OLD
;    ldrb    r0, [r0, #0x1A]
;    bx      lr
    
; NEW - High bits are top 2 bits from Egg Group 2 byte
    ldrb    r1, [r0, #0x17]
    lsr     r1, #6
    lsl     r1, #8
    ldrb    r0, [r0, #0x1A]
    orr     r0, r1
    bx      lr
    
FleeRate:
    ldrb    r0, [r0, #0x1B]
    bx      lr
    
FormStatsOffset:
    ldrh    r0, [r0, #0x1C]
    bx      lr
    
FormSpritesOffset:
    ldrh    r0, [r0, #0x1E]
    bx      lr
    
FormCount:
    add     r0, #0x20
    ldrb    r0, [r0]
    bx      lr
    
DexColor:
    add     r0, #0x21
    ldrb    r0, [r0]
    lsl     r0, #26
    lsr     r0, #26 ; get: 00111111
    bx      lr
    
UnknownFlag:
    add     r0, #0x21
    ldrb    r0, [r0]
    lsl     r0, #25
    lsr     r0, #31 ; get: 01000000
    bx      lr
    
IsArceus:
    add     r0, #0x21
    ldrb    r0, [r0]
    lsl     r0, #24
    lsr     r0, #31 ; get: 10000000
    bx      lr
    
Stage:
    ldrb    r0, [r0, #0x09]
    bx      lr
    
Height:
    ldrh    r0, [r0, #0x24]
    bx      lr
    
Weight:
    ldrh    r0, [r0, #0x26]
    bx      lr
    
TMMoves1:
    ldr     r0, [r0, #0x28]
    bx      lr
    
TMMoves2:
    ldr     r0, [r0, #0x2C]
    bx      lr
    
TMMoves3:
    ldr     r0, [r0, #0x30]
    bx      lr
    
TMMoves4:
    ldr     r0, [r0, #0x34]
    bx      lr
    
TutorMoves_Starter:
    ldr     r0, [r0, #0x38]
    bx      lr
    
TutorMoves_Red:
    ldr     r0, [r0, #0x3C]
    bx      lr
    
TutorMoves_Blue:
    ldr     r0, [r0, #0x40]
    bx      lr
    
TutorMoves_Yellow:
    ldr     r0, [r0, #0x44]
    bx      lr
    
TutorMoves_Green:
    ldr     r0, [r0, #0x48]
    bx      lr
    
ReturnZero:
    mov     r0, #0
    bx      lr