package api

import (
	"bufio"
	"os"
	"regexp"
	"strings"
	"sync"
	"time"
)

var (
	// match config line:
	//  START_PERF_COLLECT = true
	//  MAX_SIZE_OF_FLUME_THREAD = 2000		# comment
	itemPattern = regexp.MustCompile(`^[\s]*([^#=]+)[\s]*=[\s]*(.*?)([\s]*#[^'"]+)?$`)
)

// Configer config struct
type Configer struct {
	rwMutex  sync.RWMutex      // read or write need to lock
	filePath string            // file path
	modTime  time.Time         // file modify time
	config   map[string]string // store items map
}

// Keys get all keys
func (c *Configer) Keys() []string {
	c.rwMutex.RLock()
	var keys []string
	for key := range c.config {
		keys = append(keys, key)
	}
	c.rwMutex.RUnlock()
	return keys
}

// Get get value for key
func (c *Configer) Get(key string) (string, bool) {
	c.rwMutex.RLock()
	value, ok := c.config[key]
	c.rwMutex.RUnlock()
	return value, ok
}

// GetWithDefault get value for key, use defaultValue when key not exist
func (c *Configer) GetWithDefault(key string, defaultValue string) string {
	c.rwMutex.RLock()
	value, ok := c.config[key]
	c.rwMutex.RUnlock()
	if ok {
		return value
	}
	return defaultValue
}

// GetFilePath get file path
func (c *Configer) GetFilePath() string {
	return c.filePath
}

func (c *Configer) load() error {
	c.rwMutex.Lock()
	defer c.rwMutex.Unlock()

	f, err := os.Open(c.filePath)
	if err != nil {
		return err
	}
	defer f.Close()

	s, err := f.Stat()
	if err != nil {
		return err
	}
	c.modTime = s.ModTime()

	config := make(map[string]string)
	scanner := bufio.NewScanner(f)
	for scanner.Scan() {
		line := strings.TrimSpace(scanner.Text())
		match := itemPattern.FindStringSubmatch(line)
		if match != nil {
			key := strings.TrimSpace(match[1])
			value := strings.TrimSpace(match[2])
			value = strings.Trim(value, "'\" \t")
			config[key] = value
		}
	}
	c.config = config
	return nil
}

// NewConfiger init load config from file
func NewConfiger(filePath string) (*Configer, error) {
	config := &Configer{filePath: filePath}
	err := config.load()
	if err != nil {
		return nil, err
	}
	return config, nil
}
