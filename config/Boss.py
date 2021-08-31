import sys
import math

# Auto-generated code below aims at helping you parse
# the standard input according to the problem statement.

def goup(v):
    return (v[0][1]>0 or v[0][3]>0) and v[1][2] == 0 and v[0][2] < 0

def godown(v):
    return (v[4][1]>0 or v[4][3]>0) and v[3][2] == 0 and v[4][2] < 0

def goright(v):
    return (v[1][4]>0 or v[3][4]>0) and v[2][3] == 0 and v[2][4] < 0

def goleft(v):
    return (v[1][0]>0 or v[3][0]>0) and v[2][1] == 0 and v[2][0] < 0

# game loop
def firstdestruction(v):
    mybots = 0
    enemys = 0
    for i in v[1:4]:
        for j in i[1:4]:
            mybots += 1 if (j>0 and j<5) else 0
            enemys += 1 if (j<0 and j>-5) else 0
    mybots += 1 if v[2][2] > 4 else 0
    return enemys > mybots + 1

def selfdestruction(v):
    if v[2][2] > 4:
        return False
    sum = 0
    for i in v[1:4]:
        for j in i[1:4]:
            j = -4 if j<-4 else j
            j = 4 if j>4 else j
            sum += j
    return sum < 0


def attackup(v):
    if v[1][2] > -1:
        return False
    if (v[1][2] == -2 or ((v[1][2]>v[2][3] or v[2][3]>-1)  and (v[1][2]>v[3][2] or v[3][2]>-1)  and (v[1][2]>v[2][1] or v[2][1]>-1))):
        return True
    return False

def attackright(v):
    if v[2][3] > -1:
        return False
    v33 = [v[1][1:], v[2][1:], v[3][1:], v[4][1:]]
    anyanotherattack = v[3][3] > 1 and attackup(v33)
    if anyanotherattack and v[2][3] > -3:
        return False
    if(v[2][3] == -2) or ((v[2][3]>v[3][2] or v[3][2]>-1) and (v[2][3]>v[2][1] or v[2][1]> -1)):
        return True
    return False

def attackdown(v):
    if v[3][2] > -1:
        return False

    v42 = [v[2][1:], v[3][1:], v[4][1:], [0,0,0,0]]
    isupattack = attackup(v42)  if v[4][2] > 0 else False

    v31 = [[0]+v[1], [0]+v[2], [0]+v[3], [0]+v[4], [0,0,0,0,0]]
    isrightattack = attackright(v31)  if v[3][1] > 0 else False

    if (isupattack or isrightattack ) and v[3][2] > -3:
        return False
    if isupattack and isrightattack and v[3][2] > -5:
        return False

    if (v[3][2] == -2) or (v[3][2]>v[2][1] or v[2][1]>-1):
        return True
    return False

def attackleft(v):
    if v[2][1] > -1:
        return False

    v31 = [[0]+v[1], [0]+v[2], [0]+v[3], [0]+v[4], [0,0,0,0,0]]
    isupattack = attackup(v31) if v[3][1] > 0 else False

    v20 = [[0,0]+v[0], [0,0]+v[1], [0,0]+v[2], [0,0]+v[3], [0,0]+v[4]]
    isrightattack = attackright(v20) if v[2][0] > 0 else False

    v11 = [[0,0,0,0,0], [0]+v[0], [0]+v[1], [0]+v[2], [0]+v[3]]
    isdownattack = attackdown(v11) if v[1][1] > 0 else False

    if (isupattack or isrightattack or isdownattack) and v[2][1] > -3:
        return False
    if ((isupattack and (isrightattack or isdownattack)) or (isrightattack and isdownattack)) and v[2][1] > -5:
        return False
    if isupattack and isrightattack and isdownattack and v[2][1] > -7:
        return False
    return True



while True:
    visionlist = []
    number_of_robots = int(input())
    for i in range(number_of_robots):
        vision = [[], [], [], [], []]
        for j in range(5):
            for k in input().split():
                cell = int(k)
                vision[j].append(cell)
        visionlist.append(vision)

    for i in range(number_of_robots):
        vision = visionlist[i]
        # Write an action using print
        #print(vision, file=sys.stderr, flush=True)
        if firstdestruction(vision):
            print("SELFDESTRUCTION")
        elif attackup(vision):
            print("ATTACK UP")
        elif attackright(vision):
            print("ATTACK RIGHT")
        elif attackdown(vision):
            print("ATTACK DOWN")
        elif attackleft(vision):
            print("ATTACK LEFT")
        elif goup(vision):
            print("MOVE UP")
        elif godown(vision):
            print("MOVE DOWN")
        elif goright(vision):
            print("MOVE RIGHT")
        elif goleft(vision):
            print("MOVE LEFT")
        elif selfdestruction(vision):
           print("SELFDESTRUCTION")
        # GUARD | MOVE (LEFT/RIGHT/UP/DOWN) | ATTACK (LEFT/RIGHT/UP/DOWN) | SELFDESCTRUCTION <message>
        else:
            print("GUARD")