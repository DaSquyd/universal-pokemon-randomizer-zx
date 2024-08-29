; r0: void* poke
; r1: u32   field
; r2: void* dest


; set values
; unencrypted   = r7
; Block A       = r5
; Block B       = r6
; Block C       = #BLOCK_C
; Block D       = r1

#DEFINE MAX_FIELD 179

#DEFINE PUSH_STACK_SIZE 0x18 ; r3-r7, lr
#DEFINE ADD_STACK_SIZE 0x10
#DEFINE STACK_SIZE (PUSH_STACK_SIZE + ADD_STACK_SIZE)

; Stack Vars
#DEFINE FIELD (STACK_SIZE - 0x28)
#DEFINE DEST (STACK_SIZE - 0x24)
#DEFINE NUM (STACK_SIZE - 0x20)
#DEFINE BLOCK_C (STACK_SIZE - 0x1C)

    push    {r3-r7, lr}
    sub     sp, #ADD_STACK_SIZE
    
    mov     r7, r0
    str     r1, [sp, #FIELD]
    str     r2, [sp, #DEST]
    
    ; Block A
    ldr     r1, [r7, #0x00] ; pid
    mov     r2, #0 ; block num
    mov     r4, #0
    bl      ARM9::GetPokeBlockShuffle
    mov     r2, #1
    mov     r5, r0
    str     r2, [sp, #NUM]
    
    ; Block B
    ldr     r1, [r7, #0x00] ; pid
    mov     r0, r7 ; poke ptr
    mov     r2, #1 ; block num
    bl      ARM9::GetPokeBlockShuffle
    mov     r6, r0
    
    ; Block C (38)
    ldr     r1, [r7] ; pid
    mov     r0, r7 ; poke ptr
    mov     r2, #2 ; block num
    bl      ARM9::GetPokeBlockShuffle
    str     r0, [sp, #BLOCK_C]
    
    ; Block D (50)
    ldr     r1, [r7] ; pid
    mov     r0, r7 ; poke ptr
    mov     r2, #3 ; block num
    bl      ARM9::GetPokeBlockShuffle
    ldr     r2, [sp, #FIELD]
    mov     r1, r0
    cmp     r2, #MAX_FIELD
    bls     Switch
    b       ReturnZero
    
Switch:
    #SWITCH r2
    #CASE PID
    #CASE IsPartyDataDecrypted
    #CASE BoxDataDecrypted
    #CASE IsSpeciesEgg
    #CASE Checksum
    #CASE Species
    #CASE Item
    #CASE OriginalTrainerID
    #CASE TotalExp
    #CASE FriendshipOrEggSteps
    #CASE Ability
    #CASE Markings
    #CASE Region
    #CASE EV_HP
    #CASE EV_Attack
    #CASE EV_Defense
    #CASE EV_Speed
    #CASE EV_SpAtk
    #CASE EV_SpDef
    #CASE Contest_Cool
    #CASE Contest_Beauty
    #CASE Contest_Cute
    #CASE Contest_Smart
    #CASE Contest_Tough
    #CASE Contest_Sheen
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Ribbon_SinnohUnova
    #CASE Move
    #CASE Move
    #CASE Move
    #CASE Move
    #CASE MovePP
    #CASE MovePP
    #CASE MovePP
    #CASE MovePP
    #CASE MovePPUp
    #CASE MovePPUp
    #CASE MovePPUp
    #CASE MovePPUp
    #CASE MoveMaxPP
    #CASE MoveMaxPP
    #CASE MoveMaxPP
    #CASE MoveMaxPP
    #CASE IV_HP
    #CASE IV_Attack
    #CASE IV_Defense
    #CASE IV_Speed
    #CASE IV_SpAtk
    #CASE IV_SpDef
    #CASE IsEgg
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE Ribbon_Hoenn
    #CASE IsFatefulEncounter
    #CASE Gender
    #CASE FormBits
    #CASE Nature
    #CASE HasHiddenAbility
    #CASE UnusedWord_72
    #CASE PokeName
    #CASE Nickname
    #CASE IsNicknamed
    #CASE UnknownByte_76
    #CASE OriginGame
    #CASE Ribbon_Sinnoh_Contest
    #CASE Ribbon_Sinnoh_Contest
    #CASE Ribbon_Sinnoh_Contest
    #CASE Ribbon_Sinnoh_Contest
    #CASE Ribbon_Sinnoh_Contest
    #CASE Ribbon_Sinnoh_Contest
    #CASE Ribbon_Sinnoh_Contest
    #CASE Ribbon_Sinnoh_Contest
    #CASE Ribbon_Sinnoh_Contest
    #CASE Ribbon_Sinnoh_Contest
    #CASE Ribbon_Sinnoh_Contest
    #CASE Ribbon_Sinnoh_Contest
    #CASE Ribbon_Sinnoh_Contest
    #CASE Ribbon_Sinnoh_Contest
    #CASE Ribbon_Sinnoh_Contest
    #CASE Ribbon_Sinnoh_Contest
    #CASE Ribbon_Sinnoh_Contest
    #CASE Ribbon_Sinnoh_Contest
    #CASE Ribbon_Sinnoh_Contest
    #CASE Ribbon_Sinnoh_Contest
    #CASE Ribbon_Sinnoh_Contest
    #CASE OTNameIn
    #CASE OTNameOut
    #CASE DateEggReceived_Year
    #CASE DateEggReceived_Month
    #CASE DateEggReceived_Day
    #CASE DateMet_Year
    #CASE DateMet_Month
    #CASE DateMet_Day
    #CASE EggLocation
    #CASE MetAtLocation
    #CASE Pokerus
    #CASE PokeBall
    #CASE MetAtLevel
    #CASE OTGender
    #CASE EncounterType
    #CASE UnusedByte_9C
    #CASE ReturnZero ; Status TPaFBPnSSS
    #CASE Level
    #CASE ReturnZero ; Capsule Seal
    #CASE ReturnZero ; Current HP
    #CASE ReturnZero ; Max HP
    #CASE ReturnZero ; Attack Stat
    #CASE ReturnZero ; Defense Stat
    #CASE ReturnZero ; Speed Stat
    #CASE ReturnZero ; Sp. Atk Stat
    #CASE ReturnZero ; Sp. Def Stat
    #CASE ReturnZero ; Mail Message
    #CASE ReturnZero ; Unknown 0xA8
    #CASE IsSlotFilled
    #CASE IsEggBothSanityBits
    #CASE SpeciesIfFilledSlotIsEgg
    #CASE CombinedIVs
    #CASE IsNameNotGendered
    #CASE Type
    #CASE Type
    #CASE ReturnZero
    #CASE ReturnZero
    #CASE IsNPoke
    #CASE UnusedByte_B3
    
ReturnZero:
    mov     r4, #0
    b       Return
    
PID:
    ldr     r4, [r7]
    b       Return
    
IsPartyDataDecrypted:
    ldrh    r0, [r7, #0x04]
    
ReturnBit0:
    lsl     r0, #31
    
ReturnBit31:
    lsr     r4, r0, #31
    b       Return
    
BoxDataDecrypted:
    ldrh    r0, [r7, #0x04]
    b       ReturnBit1
    
IsSpeciesEgg:
    ldrh    r0, [r7, #0x04]
    
ReturnBit2:
    lsl     r0, #29
    b       ReturnBit31
    
Checksum:
    ldrh    r4, [r7, #0x06]
    b       Return
    
IsSlotFilled:
    ldrh    r0, [r5]
    cmp     r0, #0
    bne     ReturnNum
    mov     r0, #0
    str     r0, [sp, #NUM]
    
ReturnNum:
    ldr     r4, [sp, #NUM]
    b       Return
    
IsEggBothSanityBits:
    ldrh    r0, [r7, #0x04]
    lsl     r0, #29
    lsr     r4, r0, #31 ; bit 2
    bne     ReturnJump_0
    b       ReturnIsEgg
    
SpeciesIfFilledSlotIsEgg:
    ldrh    r4, [r5]
    cmp     r4, #0
    beq     ReturnJump_0
    ldr     r0, [r6, #0x10]
    lsl     r0, #1
    lsr     r0, #31 ; bit 30
    bne     ReturnEggSpecies
    
    ldrh    r0, [r7, #0x04]
    lsl     r0, #29
    lsr     r0, #31 ; bit 2
    bne     ReturnEggSpecies
    
ReturnJump_0:
    b       Return
    
ReturnEggSpecies:
    ldr     r4, =650 ; egg species
    b       Return
    
Level:
    ldrb    r1, [r6, #0x18]
    ldrh    r0, [r5, #0x00]
    ldr     r2, [r5, #0x08]
    lsl     r1, #24
    lsr     r1, #27 ; get: 00000000_00000000_00000000_11111000
    bl      ARM9::GetLevelFromExp
    b       ReturnR0
    
Species:
    ldrh    r0, [r7, #0x04]
    lsl     r0, #29
    lsr     r0, #31 ; bit 2
    beq     ReturnSpecies
    b       ReturnEggSpecies
    
ReturnSpecies:
    ldrh    r4, [r5]
    b       Return
    
Item:
    ldrh    r4, [r5, #0x02]
    ldr     r0, =638 ; Reflecting Glass
    cmp     r4, r0
    bls     ReturnJump_1
    b       ReturnZero
    
OriginalTrainerID:
    ldr     r4, [r5, #0x04]
    b       Return
    
TotalExp:
    ldr     r4, [r5, #0x08]
    b       Return
    
FriendshipOrEggSteps:
    ldrb    r4, [r5, #0x0C]
    b       Return
   
Ability:
; OLD
;    ldrb    r4, [r5, #0xD]
;    b       Return

; NEW - Updated to now use bits 6 and 7 of the Markings byte as the high bits of the ability
    ldrb    r4, [r5, #0x0E]
    lsr     r4, #6 ; get: 00000000_00000000_00000000_11000000
    lsl     r4, #8 ; set: 00000000_00000000_00000011_00000000
    ldrb    r0, [r5, #0x0D]
    orr     r4, r0
    b       Return
    
Markings:
; OLD
;    ldrb    r4, [r5, #0x0E]
;    b       Return

; NEW - Now has to exclusively use lower 6 bits
    ldrb    r4, [r5, #0x0E]
    lsl     r4, #26
    lsr     r4, #26
    b       Return
    
Region:
    ldrb    r4, [r5, #0x0F]
    b       Return
    
EV_HP:
    ldrb    r4, [r5, #0x10]
    b       Return
    
EV_Attack:
    ldrb    r4, [r5, #0x11]
    b       Return
    
EV_Defense:
    ldrb    r4, [r5, #0x12]
    b       Return
    
EV_Speed:
    ldrb    r4, [r5, #0x13]
    b       Return
    
EV_SpAtk:
    ldrb    r4, [r5, #0x14]
    b       Return
    
EV_SpDef:
    ldrb    r4, [r5, #0x15]
    b       Return
    
Contest_Cool:
    ldrb    r4, [r5, #0x16]
    b       Return
    
Contest_Beauty:
    ldrb    r4, [r5, #0x17]
    b       Return
    
Contest_Cute:
    ldrb    r4, [r5, #0x18]
    b       Return
    
Contest_Smart:
    ldrb    r4, [r5, #0x19]
    b       Return
    
Contest_Tough:
    ldrb    r4, [r5, #0x1A]
    b       Return
    
Contest_Sheen:
    ldrb    r4, [r5, #0x1B]
    b       Return
    
Ribbon_SinnohUnova:
    ldr     r2, [sp, #FIELD]
    ldr     r0, [sp, #NUM]
    sub     r2, #0x19
    mov     r1, #0
    str     r2, [sp, #FIELD]
    blx     ARM9::LeftShift64
    ldr     r3, [r5, #0x1C]
    mov     r2, #0
    and     r2, r1
    and     r3, r0
    mov     r1, #0
    mov     r0, #0
    eor     r1, r2
    eor     r0, r3
    orr     r0, r1
    bne     Ribbon_SinnohUnova_ReturnNum
    mov     r0, #0
    str     r0, [sp, #NUM]
Ribbon_SinnohUnova_ReturnNum:
    b       ReturnNum
    
Move:
    ldr     r0, [sp, #FIELD]
    sub     r0, #0x36
    str     r0, [sp, #FIELD]
    lsl     r0, #1
    ldrh    r4, [r6, r0]
    b       Return
    
MovePP:
    ldr     r0, [sp, #FIELD]
    sub     r0, #0x3A
    str     r0, [sp, #FIELD]
    add     r0, r6
    ldrb    r4, [r0, #0x08]
    b       Return
    
MovePPUp:
    ldr     r0, [sp, #FIELD]
    sub     r0, #0x3E
    str     r0, [sp, #FIELD]
    add     r0, r6
    ldrb    r4, [r0, #0x0C]
    b       Return
    
MoveMaxPP:
    ldr     r0, [sp, #FIELD]
    sub     r0, #0x42
    str     r0, [sp, #FIELD]
    lsl     r0, #1
    ldrh    r0, [r6, r0]
    cmp     r0, #0
    beq     ReturnJump_1
    ldr     r1, [sp, #FIELD]
    add     r1, r6
    ldrb    r1, [r1, #0x0C]
    bl      ARM9::GetMoveMaxPP
    
ReturnR0:
    mov     r4, r0
    b       Return
    
IV_HP:
    ldr     r0, [r6, #0x10]
    lsl     r0, #27

ReturnHigh5Bits:
    lsr     r4, r0, #27
    b       Return
    
IV_Attack:
    ldr     r0, [r6, #0x10]
    lsl     r0, #22
    b       ReturnHigh5Bits
    
IV_Defense:
    ldr     r0, [r6, #0x10]
    lsl     r0, #17
    b       ReturnHigh5Bits
    
IV_Speed:
    ldr     r0, [r6, #0x10]
    lsl     r0, #12
    b       ReturnHigh5Bits
    
IV_SpAtk:
    ldr     r0, [r6, #0x10]
    lsl     r0, #7
    b       ReturnHigh5Bits
    
IV_SpDef:
    ldr     r0, [r6, #0x10]
    lsl     r0, #2
    b       ReturnHigh5Bits
    
IsEgg:
    ldrh    r0, [r7, #0x04]
    lsl     r0, #29
    lsr     r4, r0, #31 ; bit 2
    beq     ReturnIsEgg
    
ReturnJump_1:
    b       Return
        
ReturnIsEgg:
    ldr     r0, [r6, #0x10]
    lsl     r0, #1
    b       ReturnBit31
    
IsNicknamed:
    ldr     r0, [r6, #0x10]
    b       ReturnBit31
    
Ribbon_Hoenn:
    ldr     r2, [sp, #FIELD]
    ldr     r0, [sp, #NUM]
    sub     r2, #0x4D
    mov     r1, #0
    str     r2, [sp, #FIELD]
    blx     ARM9::LeftShift64
    ldr     r3, [r6, #0x14]
    mov     r2, #0
    and     r2, r1
    and     r3, r0
    mov     r1, #0
    mov     r0, #0
    eor     r1, r2
    eor     r0, r3
    orr     r0, r1
    bne     Ribbon_Hoenn_ReturnNum
    mov     r0, #0
    str     r0, [sp, #NUM]
Ribbon_Hoenn_ReturnNum:
    b       ReturnNum
    
IsFatefulEncounter:
    ldrb    r0, [r6, #0x18]
    b       ReturnBit0
    
; TODO: investigate when gender is initially set
Gender:
; OLD
;    ldrb    r1, [r6, #0x18]
;    ldrh    r0, [r5]
;    ldr     r2, [r7]
;    lsl     r1, #24
;    lsr     r1, #27
;    bl      ARM9::ReduceGenderType
;    mov     r4, r0
;    ldrb    r0, [r6, #0x18]
;    mov     r1, #6
;    bic     r0, r1
;    lsl     r1, r4, #24
;    lsr     r1, #24
;    lsl     r1, #30
;    lsr     r1, #29
;    orr     r0, r1
;    strb    r0, [r6, #0x18]
;    mov     r0, r7
;    add     r0, #8
;    mov     r1, #0x80
;    bl      ARM9::GeneratePokeChecksum
;    strh    r0, [r7, #6]
;    b       Return

; NEW
    ldrb    r4, [r6, #0x18]
    lsl     r4, #29
    lsr     r4, #30 ; bits 1 and 2
    b       Return
    
FormBits:
    ldrb    r0, [r6, #0x18]
    lsl     r0, #24
    b       ReturnHigh5Bits
    
Nature:
    ldrb    r4, [r6, #0x19]
    b       Return
    
HasHiddenAbility:
    ldrh    r0, [r6, #0x1A]
    b       ReturnBit0
    
IsNPoke:
    ldrh    r0, [r6, #0x1A]
    
ReturnBit1:
    lsl     r0, #30
    b       ReturnBit31
    
UnusedWord_72:
    ldr     r4, [r6, #0x1C]
    b       Return
    
PokeName:
    ldrh    r0, [r7, #0x04]
    lsl     r0, #29
    lsr     r0, #31 ; bit 2 (is egg)
    beq     PokeName_IsNotEgg
    
    ldr     r0, =ARM9::Data_CurrentMsg
    ldr     r1, =0x028B ; message id
    ldr     r0, [r0] ; message data ptr
    ldr     r2, [sp, #DEST] ; str buf ptr
    bl      ARM9::GetTextFillStrBuf
    b       Return
    
PokeName_IsNotEgg:
    ldr     r0, [sp, #DEST] ; dest
    ldr     r1, [sp, #BLOCK_C] ; src
    
ReturnWCharsCopy:
    bl      ARM9::WCharsCopy
    b       Return
    
Nickname:
    ldrh    r0, [r7, #0x04]
    lsl     r0, #29
    lsr     r0, #31 ; bit 2 (is egg)
    beq     Nickname_IsNotEgg
    
    ldr     r0, =ARM9::Data_CurrentMsg
    ldr     r1, =0x028B ; message id
    ldr     r0, [r0] ; message data ptr
    ldr     r2, [sp, #DEST] ; str buf ptr
    mov     r3, #11 ; length
    bl      ARM9::FillStrBufFromFile
    b       Return
    
Nickname_IsNotEgg:
    ldr     r0, [sp, #BLOCK_C]
    ldr     r1, [sp, #DEST]
    mov     r2, #11 ; length
    
ReturnWCharsNCopy:
    bl      ARM9::WCharsNCopy
    b       Return
    
UnknownByte_76:
    ldr     r0, [sp, #BLOCK_C]
    ldrb    r4, [r0, #0x16]
    b       Return
    
OriginGame:
    ldr     r0, [sp, #BLOCK_C]
    ldrb    r4, [r0, #0x17]
    b       Return
    
Ribbon_Sinnoh_Contest:
    ldr     r2, [sp, #FIELD]
    ldr     r0, [sp, #NUM]
    sub     r2, #0x78
    mov     r1, #0
    str     r2, [sp, #FIELD]
    blx     ARM9::LeftShift64
    ldr     r2, [sp, #BLOCK_C]
    ldr     r4, [r2, #0x18]
    ldr     r2, [r2, #0x1C]
    mov     r3, r4
    and     r2, r1
    and     r3, r0
    mov     r1, #0
    mov     r0, #0
    eor     r1, r2
    eor     r0, r3
    orr     r0, r1
    bne     Ribbon_Sinnoh_Contest_ReturnNum
    mov     r0, #0
    str     r0, [sp, #NUM]
Ribbon_Sinnoh_Contest_ReturnNum:
    b       ReturnNum
    
OTNameIn:
    ldr     r0, [sp, #DEST]
    b       ReturnWCharsCopy
    
OTNameOut:
    ldr     r1, [sp, #DEST]
    mov     r2, #8
    b       ReturnWCharsNCopy
    
DateEggReceived_Year:
    ldrb    r4, [r1, #0x10]
    b       Return
    
DateEggReceived_Month:
    ldrb    r4, [r1, #0x11]
    b       Return
    
DateEggReceived_Day:
    ldrb    r4, [r1, #0x12]
    b       Return
    
DateMet_Year:
    ldrb    r4, [r1, #0x13]
    b       Return
    
DateMet_Month:
    ldrb    r4, [r1, #0x14]
    b       Return
    
DateMet_Day:
    ldrb    r4, [r1, #0x15]
    b       Return
    
EggLocation:
    ldrh    r4, [r1, #0x16]
    b       Return

MetAtLocation:
    ldrh    r4, [r1, #0x18]
    b       Return
    
Pokerus:
    ldrb    r4, [r1, #0x1A]
    b       Return
    
PokeBall:
    ldrb    r4, [r1, #0x1B]
    b       Return
    
MetAtLevel:
    ldrb    r0, [r1, #0x1C]
    lsl     r0, #25
    lsr     r4, r0, #25
    b       Return
    
OTGender:
    ldrb    r0, [r1, #0x1C]
    lsl     r0, #24
    b       ReturnBit31
    
EncounterType:
    ldrb    r4, [r1, #0x1D]
    b       Return
    
UnusedByte_9C:
    ldrb    r4, [r1, #0x1E]
    b       Return
    
CombinedIVs:
; OLD
;    ldr     r4, [r6, #0x10]
;
;    lsl     r0, r4, #2
;    lsr     r0, #27
;    lsl     r5, r0, #25
;
;    lsl     r0, r4, #7
;    lsr     r0, #27
;    lsl     r3, r0, #20
;
;    lsl     r0, r4, #12
;    lsr     r0, #27
;    lsl     r2, r0, #15
;
;    lsl     r0, r4, #17
;    lsr     r0, #27
;    lsl     r1, r0, #10
;
;    lsl     r0, r4, #27
;    lsl     r4, #22
;    lsr     r4, #27
;    lsr     r0, #27
;    lsl     r4, #5
;
;    orr     r0, r4
;    orr     r0, r1
;    orr     r0, r2
;    orr     r0, r3
;    mov     r4, r5
;    orr     r4, r0
;    b       Return

; NEW
    ldr     r4, [r6, #0x10]
    lsl     r4, #2
    lsr     r4, #2
    b       Return
    
IsNameNotGendered:
    ldrh    r0, [r5]
    cmp     r0, #29 ; Nidoran F
    beq     IsNameNotGendered_CheckIsNicknamed
    cmp     r0, #32 ; Nidoran M
    bne     ReturnOne
    
IsNameNotGendered_CheckIsNicknamed:
    ldr     r0, [r6, #0x10]
    lsr     r0, #31
    bne     ReturnOne
    b       ReturnZero
    
ReturnOne:
    mov     r4, #1
    b       Return
    
Type:
    ldrh    r0, [r5]
    ldr     r1, =493 ; Arecus
    cmp     r0, r1
    bne     Type_Standard
    
    ldrb    r1, [r5, #0x0D]
    cmp     r1, #121 ; Multitype
    bne     Type_Standard
    
    ldrh    r0, [r5, #0x02]
    bl      ARM9::GetTypeForPlate
    b       ReturnR0
    
Type_Standard:
    ldrb    r1, [r6, #0x18]
    ldr     r2, [sp, #FIELD]
    lsl     r1, #24
    sub     r2, #0xA8
    lsr     r1, #27
    str     r2, [sp, #FIELD]
    bl      ARM9::GetPersonalField
    b       ReturnR0
    
UnusedByte_B3:
    ldrb    r4, [r1, #0x1F]
    
Return:
    mov     r0, r4
    add     sp, #ADD_STACK_SIZE
    pop     {r3-r7, pc}
