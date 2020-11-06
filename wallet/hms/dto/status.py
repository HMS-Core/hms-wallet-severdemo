# -*- coding: utf-8 -*- 
# @Author    :
# @Date      :2020/6/10
# @Version   :Python 3.8.3
# @File      :status


class Status(object):
    def __init__(self):
        self.state = None
        self.effectTime = None
        self.expireTime = None

    def set_state(self, state):
        self.state = state

    def get_state(self):
        return self.state

    def set_effectTime(self, effectTime):
        self.effectTime = effectTime

    def get_effectTime(self):
        return self.effectTime

    def set_expireTime(self, expireTime):
        self.expireTime = expireTime

    def get_expireTime(self):
        return self.expireTime

